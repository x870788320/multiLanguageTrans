import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


//公共方法
public class Common {

    //读文件夹，得到文件名字
    public static List readDir ( String path){
//        String[] filePaths ;
        List<String> filePaths = new ArrayList<String>();
        File root = new File(path);
        File[] files = root.listFiles();
        for(File file: files){
            if(file.isDirectory()){
                readFile(file.getAbsolutePath());
            } else {
                filePaths.add(file.getPath());
            }
        }
        return filePaths;
    }

    //读取文件
    public static String[] readFile (String filePath){
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            Reader reader = new InputStreamReader(
                    new FileInputStream(file), "utf-8"
            );
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            String JsonString = sb.toString().split("Config.language = ")[1];
            String[] JsonArr = JsonString.substring(2, JsonString.length()-3).split(",\\n");
            return JsonArr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //创建文件夹或者文件
    public static void makeDirOrFile (String path, Document doc) {
        try {
            File file = new File(path);
            boolean isHas = file.exists();
            if(!path.contains(".") && !isHas){
                file.mkdir();
//                System.out.println("mkdir:" + path);
            } else if (path.contains(".") && !isHas){
//                file.createNewFile();
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("UTF-8");
                XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
                writer.setEscapeText(false);
                writer.write(doc);
                System.out.println("mkFile:" + path);
            }else {
                System.out.println(path + "文件存在");
            }
        } catch (IOException e){
            System.out.println(e);
        }
    }
}
