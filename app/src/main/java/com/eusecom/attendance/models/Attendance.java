package com.eusecom.attendance.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class Attendance {

    public String usico;
    public String usid;
    public String ume;
    public String dmxa;
    public String daod;
    public String dado;
    public String dnixa;
    public String hodxb;
    public String longi;
    public String lati;

    public Attendance() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }



    public Attendance(String usico, String usid, String ume, String dmxa, String daod, String dado, String dnixa,
                      String hodxb, String longi, String lati) {
        this.usico = usico;
        this.usid = usid;
        this.ume = ume;
        this.dmxa = dmxa;
        this.daod = daod;
        this.dado = dado;
        this.dnixa = dnixa;
        this.hodxb = hodxb;
        this.longi = longi;
        this.lati = lati;
    }


    public String getUsico() {

        return this.usico;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("usico", usico);
        result.put("usid", usid);
        result.put("ume", ume);
        result.put("dmxa", dmxa);
        result.put("daod", daod);
        result.put("dado", dado);
        result.put("dnixa", dnixa);
        result.put("hodxb", hodxb);
        result.put("longi", longi);
        result.put("lati", lati);

        return result;
    }
    // [END post_to_map]

}
// [END blog_user_class]
