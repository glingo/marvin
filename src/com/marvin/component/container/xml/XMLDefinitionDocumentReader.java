package com.marvin.component.container.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

import java.text.NumberFormat;
import java.text.ParsePosition;

import com.marvin.component.container.ContainerBuilder;
import com.marvin.component.container.config.Definition;
import com.marvin.component.container.config.Parameter;
import com.marvin.component.container.config.Reference;
import com.marvin.component.io.xml.XMLDocumentReader;
import com.marvin.component.io.xml.XMLReaderContext;
import com.marvin.component.util.ClassUtils;
import com.marvin.component.util.ObjectUtils;
import com.marvin.component.util.StringUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLDefinitionDocumentReader extends XMLDocumentReader {

    public static final String SERVICE_ELEMENT = "service";
    public static final String SERVICES_ELEMENT = "services";
    public static final String ARGUMENT_ELEMENT = "argument";
    public static final String PARAMETERS_ELEMENT = "parameters";
    public static final String PARAMETER_ELEMENT = "parameter";
    public static final String VALUE_ELEMENT = "value";
    public static final String TAG_ELEMENT = "tag";
    public static final String CALL_ELEMENT = "call";

    public static final String ARRAY_ELEMENT = "array";
    public static final String LIST_ELEMENT = "list";
    public static final String MAP_ELEMENT = "map";
    public static final String SET_ELEMENT = "set";

    public static final String TYPE_ATTRIBUTE = "type";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String INDEX_ATTRIBUTE = "index";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String VALUE_ATTRIBUTE = "value";

    public XMLDefinitionDocumentReader(XMLReaderContext context) {
        super(context);
    }

    public void registerDefinitions(Document doc, ContainerBuilder builder) {
        Element root = doc.getDocumentElement();
        doRegisterDefinitions(root, builder);
    }

    protected void doRegisterDefinitions(Element root, ContainerBuilder builder) {

//        preProcessXml(root);
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                parseDefinitionElement(ele, builder);
                parseExtensionElement(ele, builder);
            }
        }
//        postProcessXml(root); 

    }
    
    private void parseExtensionElement(Element ele, ContainerBuilder builder) {
        if (builder.getExtensions().containsKey(ele.getNodeName())
                || builder.getExtensions().containsKey(ele.getLocalName())) {
            processExtension(ele, builder);
        }
    }
    
    private void parseDefinitionElement(Element ele, ContainerBuilder builder) {
        
        parseElement(ele);
        if (nodeNameEquals(ele, SERVICE_ELEMENT)) {
            processDefinition(ele, builder);
        } else if (nodeNameEquals(ele, PARAMETER_ELEMENT)) {
            processParameter(ele, builder);
        } else if (nodeNameEquals(ele, SERVICES_ELEMENT)
                || nodeNameEquals(ele, PARAMETERS_ELEMENT)) {
            // recurse
            doRegisterDefinitions(ele, builder);
        }
    }

    private void parseArgumentElements(Element serviceEle, Definition definition) {
        NodeList nl = serviceEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element && nodeNameEquals(node, ARGUMENT_ELEMENT)) {
                parseArgumentElement((Element) node, definition);
            }
        }
    }

    private void parseArgumentElement(Element argEle, Definition definition) {
//        String nameAttr = argEle.getAttribute(NAME_ATTRIBUTE);
        String indexAttr = argEle.getAttribute(INDEX_ATTRIBUTE);

        Object arg = parseArgumentValue(argEle);

        if (StringUtils.hasLength(indexAttr)) {
            try {
                int index = Integer.parseInt(indexAttr);
                if (index > 0) {
                    definition.replaceArgument(index, arg);
                }
            } catch (NumberFormatException ex) {
                System.err.println("Attribute 'index' of tag 'constructor-arg' must be an integer");
            }
        } else {
            definition.addArgument(arg);
        }
    }

    public Object parseArgumentValue(Element ele) {
        String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);

        NodeList nl = ele.getChildNodes();
        Element subElement = null;
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                // Child element is what we're looking for.
                if (subElement != null) {
                    System.err.println(ele.getNodeName() + " must not contain more than one sub-element");
                } else {
                    subElement = (Element) node;
                }
            }
        }

        boolean hasRefAttribute = ele.hasAttribute(REF_ATTRIBUTE);
        boolean hasValueAttribute = ele.hasAttribute(VALUE_ATTRIBUTE);

        if (hasRefAttribute) {
            String refName = ele.getAttribute(REF_ATTRIBUTE);
            switch (typeAttr) {
                case SERVICE_ELEMENT:
                    return new Reference(refName);
                case PARAMETER_ELEMENT:
                    return new Parameter(refName);
            }
        } else if (hasValueAttribute) {
            try {
                String value = ele.getAttribute(VALUE_ATTRIBUTE);
                Class type = ClassUtils.forName(typeAttr, this.getClass().getClassLoader());
                return parser.parse(type, value);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(XMLDefinitionDocumentReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (subElement != null) {
            return parseSubElement(subElement);
        }

        return null;
    }

    protected void parseCollectionElements(NodeList elementNodes, Collection<Object> target) {

        for (int i = 0; i < elementNodes.getLength(); i++) {
            Node node = elementNodes.item(i);
            if (node instanceof Element) {
                target.add(parseSubElement((Element) node));
            }
        }
    }

    public Set<Object> parseSetElement(Element collectionEle) {
        NodeList nl = collectionEle.getChildNodes();
        Set<Object> target = new HashSet<>(nl.getLength());
        parseCollectionElements(nl, target);
        return target;
    }

    public List<Object> parseListElement(Element collectionEle) {
        NodeList nl = collectionEle.getChildNodes();
        List<Object> target = new ArrayList<>(nl.getLength());
        parseCollectionElements(nl, target);
        return target;
    }

    public Object parseArrayElement(Element arrayEle) {
        NodeList nl = arrayEle.getChildNodes();
//        int size = nl.getLength();
//        Object[] target = new Object[size];
        ArrayList<Object> l = new ArrayList();
        parseCollectionElements(nl, l);
        return l.toArray();
    }

    public Object parseSubElement(Element ele) {
        if (nodeNameEquals(ele, ARRAY_ELEMENT)) {
            return parseArrayElement(ele);
        } else if (nodeNameEquals(ele, LIST_ELEMENT)) {
            return parseListElement(ele);
        } else if (nodeNameEquals(ele, SET_ELEMENT)) {
            return parseSetElement(ele);
        } else if (nodeNameEquals(ele, VALUE_ELEMENT)) {
            return parseValueElement(ele);
        } else if (nodeNameEquals(ele, ARGUMENT_ELEMENT)) {
            return parseArgumentValue(ele);
        } else {
            System.err.println("Unknown property sub-element: [" + ele.getNodeName() + "]");
            return null;
        }
    }

    public Object parseValueElement(Element ele) {
        // It's a literal value.
        String value = ele.getTextContent();
        String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);
        
        try {
            Class type = ClassUtils.forName(typeAttr, this.getClass().getClassLoader());
            return parser.parse(type, value);
        } catch (ClassNotFoundException ex) {
            return value;
        }
    }

    protected void processParameter(Element ele, ContainerBuilder builder) {
        String id = ele.getAttribute("id");
        String valueAttr = ele.getAttribute(VALUE_ATTRIBUTE);
        String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);

        if (StringUtils.hasLength(id)) {
            try {
                Class type = ClassUtils.forName(typeAttr, this.getClass().getClassLoader());
                builder.addParameter(id, parser.parse(type, valueAttr));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(XMLDefinitionDocumentReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void processDefinition(Element ele, ContainerBuilder builder) {
        String id = ele.getAttribute("id");

        if (!StringUtils.hasLength(id)) {
            return;
        }
        
        Definition definition = new Definition();
        
        String className = ele.getAttribute("class");
        String factoryMethod = ele.getAttribute("factoryMethod");
        String factory = ele.getAttribute("factory");
        
        if (StringUtils.hasLength(factoryMethod)) {
            definition.setFactoryMethodName(factoryMethod);
        }
        
        boolean hasFactory = StringUtils.hasLength(factory);
        
        if (hasFactory) {
            definition.setFactoryName(factory);
        } else if(StringUtils.hasLength(factoryMethod)) {
            definition.setFactoryName(className);
        }
        
        definition.setClassName(className);
        parseArgumentElements(ele, definition);
        parseTagElements(ele, definition);
        parseCallElements(ele, definition);
        builder.addDefinition(id, definition);
    }
    
    private void parseCallElements(Element serviceEle, Definition definition) {
        NodeList nl = serviceEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element && nodeNameEquals(node, CALL_ELEMENT)) {
                parseCallElement((Element) node, definition);
            }
        }
    }
    
    private void parseCallElement(Element callEle, Definition definition) {
        String nameAttr = callEle.getAttribute(NAME_ATTRIBUTE);
        Object[] arguments = new Object[]{};
        
        NodeList nl = callEle.getChildNodes();
         for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element && nodeNameEquals(node, ARGUMENT_ELEMENT)) {
                arguments = ObjectUtils.addObjectToArray(arguments, parseArgumentValue((Element) node));
            }
        }

        if (StringUtils.hasLength(nameAttr)) {
            definition.addCall(nameAttr, arguments);
        }
    }
    
    private void parseTagElements(Element serviceEle, Definition definition) {
        NodeList nl = serviceEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element && nodeNameEquals(node, TAG_ELEMENT)) {
                parseTagElement((Element) node, definition);
            }
        }
    }
    
    private void parseTagElement(Element tagEle, Definition definition) {
        String nameAttr = tagEle.getAttribute(NAME_ATTRIBUTE);

        if (StringUtils.hasLength(nameAttr)) {
            definition.addTag(nameAttr);
        }
    }
    
    protected void processExtension(Element ele, ContainerBuilder builder) {
        HashMap values = (HashMap) convertElementToMap(ele, true);
        try {
            builder.loadFromExtension(ele.getNodeName(), values);
        } catch (Exception ex) {
            Logger.getLogger(XMLDefinitionDocumentReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected Object convertElementToMap(Element ele, boolean checkPrefix) {
        String prefix = ele.getPrefix();
        boolean empty = true;
        
        HashMap<String, Object> config = new HashMap<>();
        
        NamedNodeMap attributes = ele.getAttributes();
        
        for (int i = 0; i < attributes.getLength(); i++) {
            Node node = attributes.item(i);
            String nodePrefix = node.getPrefix();
            
            if(checkPrefix && prefix != null && !"".equals(nodePrefix) && !prefix.equals(nodePrefix)) {
                continue;
            }
            
            config.put(node.getNodeName(), convert(node.getNodeValue()));
            empty = false;
        }
        
        Object nodeValue = false;
        
        NodeList children = ele.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            
            if(node.getNodeType() == Node.TEXT_NODE) {
                if(!"".equals(node.getNodeValue().trim())) {
                    nodeValue = node.getNodeValue();
                    empty = false;
                }
            } else if(node.getNodeType() != Node.COMMENT_NODE){
                Object value = convertElementToMap((Element) node, checkPrefix);
                    String key = node.getLocalName();
                    if(config.containsKey(key)) {
                        if(!(config.get(key) instanceof Collection)) {
                            config.put(key, new ArrayList<>());
                        }
                        
                        ((List) config.get(key)).add(value);   
                    } else {
                        config.put(key, value);
                    }
                
                    empty = false;
            }
            
        }
        
        if(!Boolean.FALSE.equals(nodeValue)) {
            Object value = convert((String) nodeValue);
            if(config.size() > 0) {
                config.put("value", value);
            } else {
                return value;
            }
        }
        
        return !empty ? config : null;
    }
    
    
    protected Object convert(String value) {
        String lowerCase = value.toLowerCase();
        
        if(isNumeric(lowerCase)) {
            return getNumeric(value);
        }
        
        switch(lowerCase) {
            case "null":
                return null;
            case "true":
                return true;
            case "false":
                return false;
            
            default :
                return value;
        }
        
    }
    
    public static NumberFormat getNumberFormatter() {
        return NumberFormat.getInstance();
    }
    
    public static Number getNumeric(String inputData) {
        NumberFormat formatter = getNumberFormatter();
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(inputData, pos);
    }
    
    public static boolean isNumeric(String inputData) {
        NumberFormat formatter = getNumberFormatter();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(inputData, pos);
        return inputData.length() == pos.getIndex();
    }
    
    


//    public boolean isDefaultNamespace(String namespaceUri) {
//        return (!StringUtils.hasLength(namespaceUri) || BEANS_NAMESPACE_URI.equals(namespaceUri));
//    }
//    public boolean isDefaultNamespace(Node node) {
//        return isDefaultNamespace(getNamespaceURI(node));
//    }
//    private boolean isCandidateElement(Node node) {
//        return (node instanceof Element && (isDefaultNamespace(node) || !isDefaultNamespace(node.getParentNode())));
//    }
}
