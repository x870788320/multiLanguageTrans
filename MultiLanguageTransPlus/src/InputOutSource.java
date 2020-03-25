

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DataFormatException;

import netscape.javascript.JSObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



public class InputOutSource {

    private final String input = "input";
    private final String output = "output";
    private final String language = "language";
    private String templateLanguage;
    private String itemRoute;
    private String languageReadDir;
    private String inputReadDir;

    List<String> outPaths = new ArrayList<String>();
    List<String> languageArr = new ArrayList<String>();
    List<String> aaa = new ArrayList<String>();
    private HashMap<String, String> inputInfo = new HashMap<String, String>();
    private HashMap<String, String> temLanTrans = new HashMap<String, String>();

    public InputOutSource() {
        GlobalConfig global = new GlobalConfig() ;
        itemRoute = global.getParentpath();
        templateLanguage = global.getTemplateLan();
        inputReadDir = itemRoute+"\\"+input;
        languageReadDir = itemRoute+ "\\" + language;
        readInputSourceFile(languageReadDir);
        readInputSourceFile(inputReadDir);
    }

    private synchronized void readInputSourceFile(String readDir) {
        List<String> filePaths = Common.readDir(readDir);
        for(String path: filePaths ){
            String fileName = path.split("\\\\")[path.split("\\\\").length - 1];
            String name = fileName.split("\\.")[0];
            String format = fileName.split("\\.")[1];
            if( "xml".equals(format) ) {
                loadXml(path);
                Common.makeDirOrFile(itemRoute+"\\"+output+ "\\"+ name, null);
                outPaths.add(itemRoute+"\\"+output+ "\\"+ name);
            }
            if("json".equals(format)) {
                languageArr.add(name);
                if(templateLanguage.equals(name)) {
                    String[] JsonArr = Common.readFile(readDir + "\\" + fileName);
                    for(String item: JsonArr){
                        if(item.contains(":")){
                            String[] temArr = item.split(":");
                            temLanTrans.put(temArr[0], temArr[1]);
                        }
                    }
                }
            }



        }
    }


    private void loadXml(String filePath) {
        try {
            // 用来读取xml文档
            SAXReader reader = new SAXReader();
            // 读取XML文件,获得document对象
            Document doc = reader.read(new File(filePath));
            // 获取根节点
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            if(list.size() <= 0){
                throw new DocumentException("错误：请检查input下文件是否有翻译项");
            };
            for(Element em: list){
                getAllNodeInfo(em);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void getAllNodeInfo(Element em){
        List<Element> emChilds = em.elements();
        if(emChilds.size() > 0 && em.getName().equals("string-array")){
            for(Element childEm: emChilds){
                getAllNodeInfo(childEm);
//                System.out.println("string-array：" + em.getText());
            }
        } else {
            if(em.getStringValue().length() > 0 ){
                inputInfo.put(em.attributeValue("name"), em.getStringValue());
//                System.out.println("+++++++++++++++===========");
            }
        }
    }



    //hisense_audio_format_aac=AAC , menu_set=Set,...
    public HashMap<String, String> getXmlMap () {
        return inputInfo;
    }

    //SE0538 = "Magenta", SE0405 = "Input channel number", OT0058 = "Dialog Clarity",..
    public HashMap<String, String> getTemLanTransMap () {
        return temLanTrans;
    }

    public List<String> getOutputPath(){
        return outPaths;
    }

    //language_alb, language_Amharic, language_ara,..
    public List<String> getLanguageArr(){
        return languageArr;
    }

    public String getLanguagePath(){
        return languageReadDir;
    }
}
