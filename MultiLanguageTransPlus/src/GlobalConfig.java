import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class GlobalConfig {

    private final String LOACTION = "//config//global.cfg";
    private final String GlobalLanguage = "my_language";
    private final String GlobalTemLan = "templateLanguage";
    private String GlobalTemLanVal;
    private HashMap<String, String> languageMap = new HashMap<String, String>();
    private Properties properties;
    private boolean hasLoad = false;

    private String courseFile;
    private String parentpath;

    public GlobalConfig(){
        init();
    }

    /**
    * 静态方法可以任意调用，非静态只能new后点调用
    * synchronized程序锁保证同一时间只有一个在用这个方法
    */
    private synchronized void init(){
        if( !hasLoad ) {
            loadProperties();
        }
    }

    private Properties loadProperties(){
        InputStream in = null;
        File directory = new File("");
        try {
            //获得默认项目路径
            courseFile = directory.getCanonicalPath();
            //项目路径的父级
            parentpath = new File(courseFile).getParent();

            //读取配置文档
            properties = new Properties();
            in = new FileInputStream(parentpath+LOACTION);
            properties.load(in);
            if( properties.isEmpty() ) {
                throw new FileNotFoundException("错误：请检查config下的global文件");
            } else {
                hasLoad = true;
            }
        } catch ( IOException e){
            e.printStackTrace();
        } finally {
            if (in !=null ){
                try{
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        GlobalTemLanVal = properties.getProperty(GlobalTemLan);
        if(GlobalTemLanVal.contains(".")){
//            GlobalTemLanVal = properties.getProperty(GlobalTemLan).split(".")[0];
            System.out.println(GlobalTemLanVal.split("\\."));
        }
        return properties;
    }

    //参数配置的语言key
    public String getConfigLanguage (){
        if ( !hasLoad ){
            init();
        }
        String myLanguage = properties.getProperty(GlobalLanguage);
        if (myLanguage.isEmpty() || myLanguage == null || myLanguage == ""){
            System.out.println("没有配置mylanguage,默认language_6886");
            myLanguage = "language_6886";
        }
        return properties.getProperty(myLanguage);
    }


    //de=ger, hi=hin, pt=por,...
    public HashMap<String, String> getLanguageMap (){
        if ( !hasLoad ){
            init();
        }
        String myLanguage = properties.getProperty("my_language");
        if (myLanguage.isEmpty() || myLanguage == null || myLanguage == ""){
            System.out.println("没有配置mylanguage,默认language_6886");
            myLanguage = "language_6886";
        }
        String[] list =  properties.getProperty(myLanguage).split(";");
        for(String lan: list){
            String[] lanArr = lan.split(":");
            languageMap.put(lanArr[0], lanArr[1]);
        }
        return languageMap;
    }


    public String getParentpath (){
        if ( !hasLoad ){
            init();
        }
        return parentpath;
    }

    //模板语言默认美式英语
    public String getTemplateLan(){
        if ( !hasLoad ){
            init();
        }
        return GlobalTemLanVal;
    }
}
