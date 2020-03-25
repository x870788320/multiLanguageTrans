import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiLanguageTransPlus {


    /**
    需要翻译的语言
     input文件
    */
    private HashMap<String, String> languageMap = new HashMap<String, String>();
    private HashMap<String, String> inputXmlMap = new HashMap<String, String>();
    private HashMap<String, String> temLanTrans = new HashMap<String, String>();
    private HashMap<String, String> inputKeys = new HashMap<String, String>();  //存放input keys
    private HashMap<String, String> targetMap = new HashMap<String, String>();  //存放生成的xml的数据文件
    private String languageReadDir;
    private String xmlPath;
    List<String> outPaths = new ArrayList<String>();
    List<String> languageArr = new ArrayList<String>();


    public MultiLanguageTransPlus (){
        GlobalConfig global = new GlobalConfig() ;
        InputOutSource Input = new InputOutSource();

        //de=ger, hi=hin, pt=por,.. global配置语言
        languageMap = global.getLanguageMap();
        //hisense_audio_format_aac=AAC , menu_set=Set,...  input内容
        inputXmlMap = Input.getXmlMap();
        //SE0538 = "Magenta", SE0405 = "Input channel number", OT0058 = "Dialog Clarity",..  模板翻译表内容
        temLanTrans = Input.getTemLanTransMap();
        //language_alb, language_Amharic, language_ara,..   json文件列表
        languageArr = Input.getLanguageArr();
        //输出路径
        outPaths = Input.getOutputPath();
        languageReadDir = Input.getLanguagePath();

        initKeys();
        for(Object outPath:outPaths) {
            generateFile((String) outPath);
        }
    }

    //获取name-AA0000 map 数据
    private void initKeys (){
        for(Map.Entry<String,String> entry: inputXmlMap.entrySet()) {
            String name = entry.getKey();
            String content = entry.getValue();
            String transKey = getTransKey(content);
            if("-".equals(transKey)){
                transKey = content;
                //操作不匹配项
                System.out.println(content);
            }
            inputKeys.put(name, transKey);
        }
    }

    //遍历模板文件(美式英语)判断与input的value是否相等，return
    private String getTransKey (String inputContent){
        for(Map.Entry<String,String> entry: temLanTrans.entrySet()) {
            String name = entry.getKey();
            String content = entry.getValue();
            String transValue = "";
            if(content.length() > 2){
                transValue = content.substring(2,content.length()-1 );
            }
            if(inputContent.equals(transValue)){
                return name;
            }
        }
        return "-";
    }

    //创建每种语言的文件夹和xml文件
    private void generateFile (String outPath){
        //获取input文件名
        String fileName = outPath.split("\\\\")[outPath.split("\\\\").length - 1];
        boolean hasRead;
        for(Map.Entry<String,String> entry: languageMap.entrySet()) {
            String generateName = entry.getKey();
            String readName = entry.getValue();
            xmlPath = outPath+ "\\values-" + generateName+"\\" + fileName+ ".xml";
            Common.makeDirOrFile(outPath+ "\\values-" + generateName, null);
            hasRead = false;
            for(String language:languageArr ){
                if(readName.toUpperCase().equals(language.split("_")[1].toUpperCase())) {
                    readJsonFile(language);
                    hasRead = true;
                }
            }
            if(!hasRead){
                System.out.println("请确认语言"+ generateName+ ":"+ readName + "是否存在");
            }

        }
    }

    //根据不同语言读取不同json文件，根据上面生成的input文件生成目标 name -- language  map
    private void readJsonFile (String JsonFileName){
        String[] JsonArr = Common.readFile(languageReadDir + "\\" + JsonFileName+ ".json");
        for(Map.Entry<String,String> entry: inputKeys.entrySet()) {
            String name = entry.getKey();
            String content = entry.getValue();
            for(String item: JsonArr){
                if(item.contains(":")){
                    String[] temArr = item.split(":");
                    if(content.equals(temArr[0])) {
                        targetMap.put(name,temArr[1] );
                    }
                }
            }
        }
        createXML();
    }

    //用上面的目标mao生成xml文件
    private void createXML (){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("resources");
//        root.addAttribute("ver","1.0");
        for(Map.Entry<String,String> entry: targetMap.entrySet()) {
            String name = entry.getKey();
            String content = entry.getValue();
            Element string = root.addElement("string");
            string.addAttribute("name",name);
            string.setText(content);
        }
        Common.makeDirOrFile(xmlPath, document);
    }
}
