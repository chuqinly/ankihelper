package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liao on 2017/4/28.
 */

public class Dedict implements IDictionary {

    private static final String DICT_NAME = "欧路德语在线";
    private static final String DICT_INTRO = "数据来自 godic.net/mdicts/de/";
    private static final String[] EXP_ELE = new String[]{"单词", "音标", "德汉词典", "德德词典", "德英词典"};

    private static final String wordUrl = "https://www.godic.net/mdicts/de/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public Dedict(Context context) {

    }

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public String[] getExportElementsList() {
        return EXP_ELE;
    }

    public List<Definition> wordLookup(String key) {
        try {
            String headWrod = "";
            String phonetics = "";
            String frCnDef = "";
            String frFrDef = "";
            String frEnDef = "";

            Document doc = Jsoup.connect(wordUrl + key)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            Elements word = doc.select("h2.word > span.word");
            if (word.size() == 1) {
                headWrod = word.get(0).text().trim();
            }
            Elements phonitic = doc.select("span.Phonitic");
            if (phonitic.size() == 1) {
                phonetics = phonitic.get(0).text().trim();
            }

            ArrayList<String> cnDefSplitted = new ArrayList<>();

            Elements cnDiv = doc.select("#FCChild");
            if (cnDiv.size() == 1) {
                frCnDef = cnDiv.get(0).html().trim();
                for(Element cnDef : cnDiv.get(0).select("span.exp")){
                    cnDefSplitted.add(cnDef.text().trim().replace("\n","").replace("\t",""));
                }
            }

            Elements frDiv = doc.select("#FFChild");
            if (frDiv.size() == 1) {
                frFrDef = frDiv.get(0).html().trim();
            }

            Elements enDiv = doc.select("#FEChild");
            if (enDiv.size() == 1) {
                frEnDef = enDiv.get(0).html().trim();
            }

            ArrayList<Definition> defList = new ArrayList<>();
            for(String cnDef : cnDefSplitted){
                HashMap<String, String> expele = new HashMap<>();
                expele.put(EXP_ELE[0], headWrod);
                expele.put(EXP_ELE[1], phonetics);
                expele.put(EXP_ELE[2], cnDef);
                expele.put(EXP_ELE[3], frFrDef);
                expele.put(EXP_ELE[4], frEnDef);
                String exportedHtml = headWrod + " " + phonetics + "<br/>" + cnDef;
                Definition d = new Definition(expele, exportedHtml);
                defList.add(d);
            }
            return defList;
        } catch (IOException ioe) {
            //Log.d("time out", Log.getStackTraceString(ioe));
            Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
            return new ArrayList<Definition>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


}
