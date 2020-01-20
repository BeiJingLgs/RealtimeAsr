package com.hanvon.speech.realtime.bean;

public class DeviceLogin {

    /**
     * Token : BC4A914AD0A66ED9174D306D438FD07FD5B59B028431CCC3C46066E8733C0113424B191EFF46FEFA79F21952CBCD9389A58472ECDF04536ECF8C4FE35D747422846F98AA829779D5B20BC063F755DFC91725143385D53329E8938B1BAEB7526D
     * Code : 0
     * Msg : 登录成功
     */

    private String Token;
    private String Code;
    private String Msg;

    public String getToken() {
        return Token;
    }

    public void setToken(String Token) {
        this.Token = Token;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }
}
