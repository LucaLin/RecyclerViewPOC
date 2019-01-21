package com.example.r30_a.recyclerviewpoc.model;

/**
 * Created by R30-A on 2019/1/9.
 */

public class EmailData {

    private String type;
    private String mail;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
//        switch (type){
//            case "1":
//                this.type = "住家";
//             break;
//            case "2":
//                this.type = "公司";
//                break;
//            case "3":
//                this.type = "其它";
//                break;
//            case "0":
//                this.type = "自訂";
//                break;
//        }
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


}
