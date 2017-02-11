package com.magenta.mc.client.android.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.magenta.mc.client.android.util.StringUtils;

import java.io.Serializable;

@DatabaseTable(tableName = "localize_string")
public class LocalizeStringEntity extends AbstractEntity implements Serializable {

    @DatabaseField(columnDefinition = "en")
    private String en;
    @DatabaseField(columnDefinition = "ru")
    private String ru;
    @DatabaseField(columnDefinition = "es")
    private String es;
    @DatabaseField(columnDefinition = "fr")
    private String fr;

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public String getLocalizeString(LocalizeStringType type) {
        String result;
        switch (type) {
            case EN: {
                result = en;
                break;
            }
            case RU: {
                result = ru;
                break;
            }
            case FR: {
                result = fr;
                break;
            }
            case ES: {
                result = es;
                break;
            }
            default: {
                result = en;
            }
        }
        return StringUtils.isBlank(result) ? en : result;
    }

    @Deprecated
    public Object toRecord() {
        return null;
    }

    public enum LocalizeStringType {
        EN, RU, ES, FR;

        public static LocalizeStringType getType(String type) {
            if (type.equalsIgnoreCase(RU.name())) {
                return RU;
            } else if (type.equalsIgnoreCase(ES.name())) {
                return ES;
            } else if (type.equalsIgnoreCase(FR.name())) {
                return FR;
            } else {
                return EN;
            }
        }
    }
}