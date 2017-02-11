package com.lo.tinymvc.util;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/2/8.
 */
public abstract class PathUtil {

    private static final String PATH_SEPARATOR = "/";
    private static final char[] WILDCARD_CHARS = { '*', '?', '{' };
    private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");
    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

    public static void addUrlsForPath(Set<String> urls, String path) {
        urls.add(path);
        if (path.indexOf('.') == -1 && !path.endsWith("/")) {
            urls.add(path + ".*");
            urls.add(path + "/");
        }
    }

    public static String getPathWithinApplication(HttpServletRequest request){
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        return getRemainingPath(requestUri,contextPath);
    }

    private static String getRemainingPath(String longPath,String shortPath){
        return longPath.substring(shortPath.length());
    }

    private static String[] toTokenPath(String path){
        String processedPath = new String(path);
        if(path.startsWith(PATH_SEPARATOR)){
            processedPath = processedPath.substring(1);
        }
        return processedPath.split(PATH_SEPARATOR);
    }

    public static boolean pathMatch(String pattern,String path){
        //先判断是两者的起始字符是否都是或者都不是分隔符
        if(path.startsWith(PATH_SEPARATOR) != pattern.startsWith(PATH_SEPARATOR)){
            return false;
        }
        String[] pattDirs = toTokenPath(pattern);
        if (!isPotentialMatch(path, pattDirs)){
            return false;
        }

        String[] pathDirs = toTokenPath(path);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd){
            String pattDir = pattDirs[pattIdxStart];
            if ("**".equals(pattDir)) {
                break;
            }
            if(!matchString(pattDir,pathDirs[pathIdxStart])){
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }
        if (pathIdxStart > pathIdxEnd){
            if (pattIdxStart > pattIdxEnd){
                return (pattern.endsWith(PATH_SEPARATOR) == path.endsWith(PATH_SEPARATOR));
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(PATH_SEPARATOR)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        }
        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxEnd];
            if (pattDir.equals("**")) {
                break;
            }
            if (!matchString(pattDir, pathDirs[pathIdxEnd])) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (pattDirs[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchString(subPat, subStr)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!pattDirs[i].equals("**")) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPotentialMatch(String path, String[] pattDirs){
        char[] pathChars = path.toCharArray();
        int pos = 0;
        for (String pattDir : pattDirs) {
            int skipped = skipSeparator(path, pos, PATH_SEPARATOR);
            pos += skipped;
            skipped = skipSegment(pathChars, pos, pattDir);
            if (skipped < pattDir.length()) {
                return skipped > 0 || (pattDir.length() > 0) && isWildcardChar(pattDir.charAt(0));
            }
            pos += skipped;
        }
        return true;
    }

    //返回path从pos开始的跳过sparator所需跳的次数
    private static int skipSeparator(String path, int pos, String separator) {
        int skipped = 0;
        while (path.startsWith(separator, pos + skipped)) {
            skipped += separator.length();
        }
        return skipped;
    }

    //返回chars从pos开始的跳过prefix所需要跳的次数，
    // 在跳的过程中如果遇到特殊符号就立即返回当前跳的次数
    //如果一直跳到chars的结尾还没跳完prefix，就返回0
    private static int skipSegment(char[] chars, int pos, String prefix) {
        int skipped = 0;
        for (char c : prefix.toCharArray()) {
            if (isWildcardChar(c)) {
                return skipped;
            }
            else if (pos + skipped >= chars.length) {
                return 0;
            }
            else if (chars[pos + skipped] == c) {
                skipped++;
            }
        }
        return skipped;
    }

    private static boolean isWildcardChar(char c) {
        for (char candidate : WILDCARD_CHARS) {
            if (c == candidate) {
                return true;
            }
        }
        return false;
    }

    private static Pattern getPattern(String pattern){
        StringBuilder patternBuilder = new StringBuilder();
        Matcher matcher = GLOB_PATTERN.matcher(pattern);
        int end = 0;
        while (matcher.find()) {
            patternBuilder.append(quote(pattern, end, matcher.start()));
            String match = matcher.group();
            if ("?".equals(match)) {
                patternBuilder.append('.');
            }
            else if ("*".equals(match)) {
                patternBuilder.append(".*");
            }
            else if (match.startsWith("{") && match.endsWith("}")) {
                int colonIdx = match.indexOf(':');
                if (colonIdx == -1) {
                    patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                }
                else {
                    String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                    patternBuilder.append('(');
                    patternBuilder.append(variablePattern);
                    patternBuilder.append(')');
                }
            }
            end = matcher.end();
        }
        patternBuilder.append(quote(pattern, end, pattern.length()));
        return Pattern.compile(patternBuilder.toString());
    }

    private static String quote(String s, int start, int end) {
        if (start == end) {
            return "";
        }
        return Pattern.quote(s.substring(start, end));
    }

    private static boolean matchString(Pattern pattern,String str){
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private static boolean matchString(String pattern,String str){
        return matchString(getPattern(pattern),str);
    }

    public static Set<String> generatePathPatterns(String path){
        Set<String> pathPatterns = new LinkedHashSet<String>();
        addUrlsForPath(pathPatterns,path);
         return pathPatterns;
    }

    public static Set<String> generateCombinedPathPatterns(String[] mainPaths,String[] subPaths){
        Set<String> pathPatterns = new LinkedHashSet<String>();
        for(String mainPath : mainPaths){
            for(String subPath : subPaths){
                String combinePath = mainPath + subPath;
                pathPatterns.addAll(generatePathPatterns(combinePath));
            }
        }
        return pathPatterns;
    }
}
