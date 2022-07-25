package cn.xxstudy.dart.generatewidget.generate;

import cn.xxstudy.dart.generatewidget.utils.Utils;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DartGenerator {
    private String name;
    private boolean createStateful;

    private String templateString;
    private Map<String, String> templateMap = new HashMap<>();

    public DartGenerator(String name, boolean createStateful) {
        this.name = name;
        this.createStateful = createStateful;
        templateMap.put("className", className());
        templateMap.put("fileName", fileName());
        templateMap.put("date_time",Utils.createCurrentTime());
        init();
    }

    private void init() {
        try {
            String fileName = (createStateful) ? "dart_stateful.dart.template" : "dart_stateless.dart.template";
            String resource = "/templates/" + fileName;

            InputStream resourceAsStream = DartGenerator.class.getResourceAsStream(resource);
            templateString = CharStreams.toString(new InputStreamReader(resourceAsStream, Charsets.UTF_8));
        } catch (Exception e) {
        }
    }

    public String generate() {
        return new StrSubstitutor(templateMap).replace(templateString);
    }

    public String fileName() {
        return String.format("%s.dart", name);
    }

    private String className() {
        return Utils.underlineToHump(name);
    }

}
