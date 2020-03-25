public class main {
    public static void main (String[] args) {
        long startTime = System.currentTimeMillis() / 1000;
        MultiLanguageTransPlus m = new MultiLanguageTransPlus();

//        InputAndOut m = new InputAndOut();


        //测试Scanner的用法，可以拿到用户输入的值
        /*String in;
        System.out.println("请输入字符");
        Scanner scanner = new Scanner(System.in);
        System.out.println(scanner.next());*/
        long endTime = System.currentTimeMillis() / 1000;
        long time = endTime - startTime;
        System.out.println(time);
    }
}
