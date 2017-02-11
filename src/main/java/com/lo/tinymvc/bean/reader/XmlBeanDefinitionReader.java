package com.lo.tinymvc.bean.reader;

import com.lo.tinymvc.bean.*;
import com.lo.tinymvc.bean.scanner.ClassPathBeanDefinitionScanner;
import com.lo.tinymvc.io.ResourceLoader;
import com.lo.tinymvc.util.ClassUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/6.
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    private static final String BEAN_NAME = "bean";

    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {
        super(resourceLoader,registry);
    }

    @Override
    public void loadBeanDefinitions(String... locations) throws Exception {
        for(String location:locations){
            loadBeanDefinitions(location);
        }
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
        doLoadBeanDefinitions(inputStream);
    }
    private void doLoadBeanDefinitions(InputStream inputStream) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);

        registerBeanDefinitions(doc);
        inputStream.close();
    }

    private void registerBeanDefinitions(Document doc) {
        Element root = doc.getDocumentElement();
        parseBeanDefinitions(root);
    }

    private void parseBeanDefinitions(Element root){
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                if(isDefaultNameSpace(ele)){
                    parseDefaultElement(ele);
                }
                else{
                    parseCustomElement(ele);
                }
            }
        }
    }

    private void parseDefaultElement(Element ele){
        String name = ele.getAttribute("id");
        String className = ele.getAttribute("class");
        if(name == null || name.length() == 0){
            name = ClassUtil.getShortClassName(className);
        }
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        processProperty(ele, beanDefinition);
        beanDefinition.setBeanClassName(className);
        beanDefinition.setBeanClass(ClassUtil.getClassByName(className));
        this.getRegistry().registerBeanDefinition(name,beanDefinition);
    }

    private void processProperty(Element ele, BeanDefinition beanDefinition) {
        NodeList propertyNode = ele.getElementsByTagName("property");
        for (int i = 0; i < propertyNode.getLength(); i++) {
            Node node = propertyNode.item(i);
            if (node instanceof Element) {
                Element propertyEle = (Element) node;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                if (value != null && value.length() > 0) {
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
                } else {
                    String ref = propertyEle.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException("Configuration problem: <property> element for property '"
                                + name + "' must specify a ref or value");
                    }
                    //bean对其他对象的引用，直接放到自己的属性里面
                    BeanReference beanReference = new BeanReference(ref);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                }
            }
        }
    }

    private void parseCustomElement(Element ele){
        String basePackage = ele.getAttribute(BASE_PACKAGE_ATTRIBUTE);
        String[] basePackges = basePackage.split(",");
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner();
        Set<BeanDefinition> beanDefinitions = scanner.doScan(basePackges);
        for(BeanDefinition beanDefinition : beanDefinitions){
            String beanName = scanner.generateBeanName(beanDefinition);
            this.getRegistry().registerBeanDefinition(beanName,beanDefinition);
        }

    }

    private boolean isDefaultNameSpace(Node node){
        String nameSpace = node.getNodeName();
        return (nameSpace != null && nameSpace.length()>0 && nameSpace.equals(BEAN_NAME));
    }

}
