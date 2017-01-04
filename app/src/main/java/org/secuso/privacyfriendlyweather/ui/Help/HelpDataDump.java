package org.secuso.privacyfriendlyweather.ui.Help;

import android.content.Context;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class structure taken from tutorial at http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * last access 27th October 2016
 */

public class HelpDataDump {

    private Context context;

    public HelpDataDump(Context context) {
        this.context = context;
    }

    public LinkedHashMap<String, List<String>> getDataGeneral() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
//TODO: Strings for new help page
//        List<String> general = new ArrayList<String>();
//        general.add(context.getResources().getString(R.string.help_whatis_answer));
//
//        expandableListDetail.put(context.getResources().getString(R.string.help_whatis), general);
//
//        List<String> features = new ArrayList<String>();
//        features.add(context.getResources().getString(R.string.help_feature_one_answer));
//
//        expandableListDetail.put(context.getResources().getString(R.string.help_feature_one), features);
//
//        List<String> privacy = new ArrayList<String>();
//        privacy.add(context.getResources().getString(R.string.help_privacy_answer));
//
//        expandableListDetail.put(context.getResources().getString(R.string.help_privacy), privacy);
//
//        List<String> permissions = new ArrayList<String>();
//        permissions.add(context.getResources().getString(R.string.help_permission_answer));
//
//        expandableListDetail.put(context.getResources().getString(R.string.help_permission), permissions);

        return expandableListDetail;
    }

}
