package com.example.Code_Sharing_Solution.Controller;

import com.example.Code_Sharing_Solution.Entities.Code;
import com.example.Code_Sharing_Solution.Entities.Code_For_Display;
import com.example.Code_Sharing_Solution.Repository.CodeRepository;
import com.example.Code_Sharing_Solution.Repository.CodeRepositoryStringID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class AppController {

    @Autowired
    CodeRepository codeRepository;

    @Autowired
    CodeRepositoryStringID codeRepositoryStringID;
    Data data1;
    LocalDateTime localDateTime;

    private String TEXT = " public static void main(String[] args) {\n" +
            "        SpringApplication.run(CodeSharingPlatform.class, args);\n" +
            "    }";


    private String HTML_TEXT = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>Code</title>\n" +
            "<link rel=\"stylesheet\"\n" +
            "       href=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css\">\n" +
            "<script src=\"//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js\"></script>\n" +
            "<script>hljs.initHighlightingOnLoad();</script>" +
            "  </head>\n" +
            "  <body>\n" +
            "%s" +
            "  </body>\n" +
            "</html>";

    private String HTML_TEXT_LATEST = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>Latest</title>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "%s" +
            "  </body>\n" +
            "</html>";


    public static final Logger LOGGER = LoggerFactory.getLogger(LoggingApplicationListener.class);
    private String HTML_TEXT_NEW ="<!DOCTYPE html>" +
            "            <html>" +
            "             <head> +" +
            "            <meta charset=\"utf-8\"> " +
            "            <title>Create</title>" +
            "            </head> " +
            "            <body> " +
            "<form>" +
            "<textarea id=\"code_snippet\">  </textarea> <br>" +
            "<label for=\"time_restriction\">Time restriction: </label>" +
            "<input id=\"time_restriction\" type=\"text\"/> <br>"+
            "<label for=\"views_restriction\">Views restriction: </label>" +
            "<input id=\"views_restriction\" type=\"text\"/> <br>"+
            "<button id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>\n" +
            "" +
            "<script>" +
            "function send() {" +
            "    let object = {" +
            "        \"code\": document.getElementById(\"code_snippet\").value" +
            "    };" +
            "    " +
            "    let json = JSON.stringify(object);" +
            "    " +
            "    let xhr = new XMLHttpRequest();" +
            "    xhr.open(\"POST\", '/api/code/new', false)" +
            "    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');" +
            "    xhr.send(json);" +
            "    " +
            "    if (xhr.status == 200) {" +
            "      alert(\"Success!\");" +
            "    }" +
            "}" +
            "</script>" +
            "</form>" +
            "</body>" +
            "</html>";
    private static final String DATE_FORMATTER= "yyyy/MM/dd HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    private String CODE = " public static void main(String[] args) {\n" +
            "        SpringApplication.run(CodeSharingPlatform.class, args);\n" +
            "    }";


    Map<Long, CodeData> storeCodeData = new LinkedHashMap<>();



    @GetMapping("/api/code/{id}")
    public ResponseEntity<Code_For_Display> getCode3(@PathVariable String id)
    {
        LOGGER.info("Spring boot Get Mapping : /api/code/{id}");
        Optional<Code> getCode = codeRepositoryStringID.findById(id);
        if(getCode.isPresent())
        {
            Code code_Object = getCode.get();
            var IsTimeSpecification = code_Object.isTimeAvailable();
            var IsViewSpecification = code_Object.isViewsAvailable();

            Code_For_Display code_for_display = new Code_For_Display();
            code_for_display.setCode(code_Object.getCode());
            code_for_display.setTime(code_Object.getTime());
            code_for_display.setViews(code_Object.getViews());

            var getViews = code_Object.getViews();
            final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
            var OriginalTime = LocalDateTime.parse(code_Object.getDate(), formatter);

            var remainingTime = 0l;
            if(IsTimeSpecification){
                var remainingTimeInSecond = OriginalTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);

                remainingTime = code_Object.getTime() - remainingTimeInSecond;
                code_for_display.setTime(remainingTime);

                if(remainingTime <= 0 ){
                    codeRepositoryStringID.deleteById(code_Object.getId());  // NOT ALIEN
                    //code_Object.setTimeAvailable(false);   // ALIEN
                    return  ResponseEntity.notFound().build();
                }else {
                    codeRepositoryStringID.save(code_Object);
                }
            }

            if(IsViewSpecification) {
                if (getViews >= 2) {
                    code_for_display.setViews(getViews - 1);
                    code_Object.setViews(getViews - 1);
                    codeRepositoryStringID.save(code_Object);
                } else if (getViews <= 1) {
                    codeRepositoryStringID.deleteById(code_Object.getId()); //  NOT ALIEN
                    //code_Object.setViewsAvailable(false);   // ALIEN
                    return ResponseEntity.notFound().build();

                }
            }


            code_for_display.setDate(LocalDateTime.parse(code_Object.getDate(), formatter));
            return new ResponseEntity<>(code_for_display, HttpStatus.OK);
        }
        else {
            return  ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/api/code/new")
    public ResponseEntity<Id_Response> post1(@RequestBody CodeData codeData)
    {
        LOGGER.info("Spring boot Post Mapping : /api/code/new");
        var identification_no = UUID.randomUUID();
        Code code = new Code();
        code.setId(String.valueOf(identification_no));
        code.setCode(codeData.getCode());
        code.setDate(LocalDateTime.now());
        code.setTime(codeData.getTime());
        code.setViews(codeData.getViews());

        if(codeData.getTime() > 0){
            code.setTimeAvailable(true);
        }else {
            code.setTimeAvailable(false);
        }

        if(codeData.getViews() > 0){
            code.setViewsAvailable(true);
        }else {
            code.setViewsAvailable(false);
        }

        Code cod = codeRepositoryStringID.save(code);

        Id_Response id_response = new Id_Response();
        id_response.setId(cod.getId());
        return new ResponseEntity<>(id_response, HttpStatus.OK);

    }



    @GetMapping(value = "/code/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getCode33(@PathVariable String id){
        var format_with_View_Time = "<span id=\"load_date\"> %s </span> <br>" +
                "<span id=\"time_restriction\"> %s </span> <br> " +
                "<span id=\"views_restriction\"> %s </span> <br> " +
                "<pre id=\"code_snippet\"> <code>%s </code></pre>";

        var format_No_View = "<span id=\"load_date\"> %s </span> <br>" +
                "<span id=\"time_restriction\"> %s </span> <br> " +
                "<pre id=\"code_snippet\"> <code>%s </code></pre>";

        var format_No_Time = "<span id=\"load_date\"> %s </span> <br>" +
                "<span id=\"views_restriction\"> %s </span> <br> " +
                "<pre id=\"code_snippet\"> <code>%s </code></pre>";

        var format= "<span id=\"load_date\"> %s </span> <br>" +
                "<pre id=\"code_snippet\"> <code>%s </code></pre>";

        Optional<Code> getCODE = codeRepositoryStringID.findById(id);



        Code_For_Display code_for_display = new Code_For_Display();
        //Logger logger = (Logger) LoggerFactory.getLogger(RestController.class);

        if(getCODE.isPresent()) {
            var codeVal = getCODE.get();

            var view = codeVal.getViews();
            var time = codeVal.getTime();
            if(codeVal.isTimeAvailable() && codeVal.isViewsAvailable())
            {
                if (view >= 2) {
                    --view;
                    codeVal.setViews(view);
                    code_for_display.setViews(view);
                    codeRepositoryStringID.save(codeVal);
                } else {
                    codeRepositoryStringID.deleteById(codeVal.getId());
                    //logger.info("log : erro1");
                    System.out.println("error1");
                    return ResponseEntity.notFound().build();
                }

                final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                var OriginalTime = LocalDateTime.parse(codeVal.getDate(), formatter);

                var remainingTime = 0l;

                //Code_For_Display code_for_display = new Code_For_Display();
                long growingTime = 0;
                if (codeVal.isTimeAvailable()) {
                    growingTime = OriginalTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);

                    remainingTime = codeVal.getTime() - growingTime;
                    code_for_display.setTime(remainingTime);


                    if (remainingTime <= -1) { //if (remainingTime <= 0) {
                        codeRepositoryStringID.deleteById(codeVal.getId());
                        // logger.info("log : erro2");
                        System.out.println("error2");
                        return ResponseEntity.notFound().build();
                    } else {
                        codeRepositoryStringID.save(codeVal);
                    }
                }


                var time_restriction = "The code will be available for %s seconds";
                time_restriction = String.format(time_restriction, remainingTime);

                var view_restriction = "%s more views allowed";
                view_restriction = String.format(view_restriction, view);


                var final_String = String.format(format_with_View_Time, codeVal.getDate(),
                        time_restriction, view_restriction, codeVal.getCode());
                var final_String2 = String.format(HTML_TEXT, final_String);
                return new ResponseEntity<>(final_String2, HttpStatus.OK);
            }
            else if (codeVal.isTimeAvailable() && !codeVal.isViewsAvailable())
            {


                final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                var OriginalTime = LocalDateTime.parse(codeVal.getDate(), formatter);

                var remainingTime = 0l;

                //Code_For_Display code_for_display = new Code_For_Display();
                long growingTime = 0;
                if (codeVal.isTimeAvailable())
                {
                    growingTime = OriginalTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);

                    remainingTime = codeVal.getTime() - growingTime;
                    code_for_display.setTime(remainingTime);


                    if (remainingTime <= -1) {  // if (remainingTime <= 0) { former
                        // logger.info("log : erro3");
                        System.out.println("error3");
                        codeRepositoryStringID.deleteById(codeVal.getId());
                        return ResponseEntity.notFound().build();
                    } else {
                        codeRepositoryStringID.save(codeVal);
                    }
                }

                var time_restriction = "The code will be available for %s seconds";
                time_restriction = String.format(time_restriction, remainingTime);

                var final_String = String.format(format_No_View, codeVal.getDate(), time_restriction,
                        codeVal.getCode());
                var final_String2 = String.format(HTML_TEXT, final_String);
                return new ResponseEntity<>(final_String2, HttpStatus.OK);
            }
            else if (!codeVal.isTimeAvailable() && codeVal.isViewsAvailable())
            {
                if (view >= 2) {
                    --view;
                    codeVal.setViews(view);
                    code_for_display.setViews(view);
                    codeRepositoryStringID.save(codeVal);
                } else if (view == 1) {
                    --view;
                    codeVal.setViews(view);
                    code_for_display.setViews(view);
                    codeRepositoryStringID.save(codeVal);
                } else {
                    //logger.info("log : erro4");
                    //System.out.println("error4");

                    // place of ERROR
                    codeRepositoryStringID.deleteById(codeVal.getId());
                    return ResponseEntity.notFound().build();
                }


                var view_restriction = "%s more views allowed";
                view_restriction = String.format(view_restriction, view);


                var final_String = String.format(format_No_Time, codeVal.getDate(), view_restriction,
                        codeVal.getCode());
                var final_String2 = String.format(HTML_TEXT, final_String);
                return new ResponseEntity<>(final_String2, HttpStatus.OK);
            }
            else
            {
                {
                    var final_String = String.format(format, codeVal.getDate(), codeVal.getCode());
                    var final_String2 = String.format(HTML_TEXT, final_String);
                    return new ResponseEntity<>(final_String2, HttpStatus.OK);
                }
            }

        }else {
            //logger.info("log : erro5");
            System.out.println("error5");
            return ResponseEntity.notFound().build();
        }
    }




    /////////////////////////////////// THIRD DONE FINAL DONE
    @GetMapping("/api/code/latest")
    public ResponseEntity<Code_For_Display[]> getCode4(){


        var listCode = codeRepository.findAll().iterator();



        List<Code> list_code = new LinkedList();

        while (listCode.hasNext()){
            list_code.add(listCode.next());
        }
        var listofData2 = list_code.stream()
                .collect(Collectors.toList());



        var arrayData2 = listofData2.stream().toArray(Code[]::new);



        List<Code> sortCode2 = new LinkedList<>();


        for(int i = arrayData2.length - 1; i >= 0 ; i--){
            sortCode2.add(arrayData2[i]);
        }
        sortCode2 = sortCode2.stream()
                .filter(x -> !x.isViewsAvailable() && !x.isTimeAvailable())
                .collect(Collectors.toList());


        sortCode2 = sortCode2.stream().limit(10).collect(Collectors.toList());



        var arrayValue2 = sortCode2.stream()
//                .filter(x -> !x.isViewsAvailable() && !x.isTimeAvailable())
                .toArray(Code[]::new);

//        var arrayValue3 = Arrays.stream(arrayValue2).map(x ->{
//            Code_For_Display code_for_display = new Code_For_Display();
//            code_for_display.setCode(x.getCode());
//            code_for_display.setDate(LocalDateTime.parse(x.getDate()));
//            return code_for_display;
//       }).toArray(Code_For_Display[]::new);
        var getIterator =  Arrays.stream(arrayValue2).iterator();
        Code_For_Display[] arrayValue4 = new Code_For_Display[arrayValue2.length];

        int count = 0;
        while (getIterator.hasNext()){
            var getIt = getIterator.next();
            //if(!getIt.isTimeAvailable() && !getIt.isViewsAvailable())
            Code_For_Display cc = new Code_For_Display();
            cc.setCode(getIt.getCode());
            final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            cc.setDate(LocalDateTime.parse(getIt.getDate(), FORMATTER));
            cc.setViews(getIt.getViews());
            cc.setTime(getIt.getTime());
            arrayValue4[count] = cc;
            ++count;
        }


        //Code_For_Display[] arrayValue5 = new Code_For_Display[0];
        return new ResponseEntity<>(arrayValue4, HttpStatus.OK);

    }

    /////////////////////////////////// FOURTH DONE FINAL Doing
    @GetMapping(value = "/code/latest", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getCode5(){

        var format = "  \"<span id=\\\"load_date\\\"> %s </span>\" \n" +
                "            \"<pre id=\\\"code_snippet\\\"> %s </pre>\" ";

        StringBuilder sb = new StringBuilder();
        String ss = "";

        // REMOVE
        var iter_Data= storeCodeData.entrySet().iterator();

        var listCode = codeRepository.findAll().iterator();

        //remove
        var index = storeCodeData.entrySet().size() - 1;
        //remove
        var increment = 0;
        //remove
        List<String> list = new LinkedList<>();
        //remove
        while(iter_Data.hasNext()){
            var data = iter_Data.next().getValue();

            var str = String.format(format, data.getDate(), data.getCode());
            //ss = str + ss;
            list.add(0, str);
        }

        List<String> getListFromIterator = new LinkedList<>();
        while (listCode.hasNext()){

            var data = listCode.next();

            if(!data.isViewsAvailable() && !data.isTimeAvailable()){
                var str = String.format(format, data.getDate(), data.getCode());
                //ss = str + ss;
                getListFromIterator.add(0, str);
            }
            // var str = String.format(format, data.getDateString(), data.getCode());

        }



        //remove
        var data = list.stream().limit(10)
                .collect(Collectors.toCollection(LinkedList::new));

        var data2 = getListFromIterator.stream().limit(10)
                .collect(Collectors.toCollection(LinkedList::new));


        //remove
        var str_print = "";
        for(int i = 0 ; i < data.size(); i++){
            str_print = str_print + data.get(i);
        }

        var str_print2 = "";
        for(int i = 0 ; i < data2.size(); i++){
            str_print2 = str_print2 + data2.get(i);
        }


        //remove
        var final_String  = String.format(HTML_TEXT_LATEST, str_print);

        var final_String2  = String.format(HTML_TEXT_LATEST, str_print2);

        return new ResponseEntity<>(final_String2, HttpStatus.OK);

    }






    //@GetMapping("/api/code/new") may need modification
    @GetMapping(value = "/code/new", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCode3(){
        return new ResponseEntity<>(HTML_TEXT_NEW, HttpStatus.OK);
    }


}


class Data{
    private String code;

    private String date;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}


class CodeData {


    private String code;

    private long time;

    private long views;

    private String date;

    public CodeData(){}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public void setDate(LocalDateTime localDateTime) {
        final String DATE_FORMATTER= "yyyy/MM/dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String formatDateTime = localDateTime.format(formatter);
        this.date = formatDateTime;
    }



//    public String getDateString() {
//    }
}




class EmptyResponse {

    String empty;

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }
}


class Id_Response{
    String id;



    Id_Response(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}