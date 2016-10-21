package com.magenta.rx.rxa.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magenta.rx.rxa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONView extends LinearLayout {

//    private final String jsonStr = "{\"head\":{},\"def\":[{\"text\":\"run\",\"pos\":\"verb\",\"ts\":\"rʌn\",\"tr\":[{\"text\":\"работать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"syn\":[{\"text\":\"действовать\",\"pos\":\"глагол\",\"asp\":\"несов\"},{\"text\":\"эксплуатировать\",\"pos\":\"глагол\",\"asp\":\"несов\"}],\"mean\":[{\"text\":\"operate\"}],\"ex\":[{\"text\":\"run continuously\",\"tr\":[{\"text\":\"работать непрерывно\"}]}]},{\"text\":\"бегать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"syn\":[{\"text\":\"бежать\",\"pos\":\"глагол\",\"asp\":\"несов\"},{\"text\":\"побежать\",\"pos\":\"глагол\",\"asp\":\"сов\"},{\"text\":\"убегать\",\"pos\":\"глагол\",\"asp\":\"несов\"}],\"mean\":[{\"text\":\"running\"},{\"text\":\"flee\"},{\"text\":\"run away\"}],\"ex\":[{\"text\":\"run faster\",\"tr\":[{\"text\":\"бегать быстрее\"}]},{\"text\":\"run away\",\"tr\":[{\"text\":\"бежать прочь\"}]},{\"text\":\"run directly\",\"tr\":[{\"text\":\"побежать сразу\"}]}]},{\"text\":\"проходить\",\"pos\":\"глагол\",\"syn\":[{\"text\":\"проводить\",\"pos\":\"глагол\"},{\"text\":\"пробегать\",\"pos\":\"глагол\"},{\"text\":\"выполнить\",\"pos\":\"глагол\",\"asp\":\"сов\"},{\"text\":\"выполнять\",\"pos\":\"глагол\",\"asp\":\"несов\"},{\"text\":\"пробежать\",\"pos\":\"глагол\",\"asp\":\"сов\"}],\"mean\":[{\"text\":\"pass\"},{\"text\":\"perform\"},{\"text\":\"nurse\"}],\"ex\":[{\"text\":\"run daily\",\"tr\":[{\"text\":\"проходить ежедневно\"}]},{\"text\":\"run experiments\",\"tr\":[{\"text\":\"проводить эксперименты\"}]},{\"text\":\"run past\",\"tr\":[{\"text\":\"пробегать мимо\"}]},{\"text\":\"run the program\",\"tr\":[{\"text\":\"выполнить программу\"}]},{\"text\":\"run errands\",\"tr\":[{\"text\":\"выполнять поручения\"}]},{\"text\":\"run a mile\",\"tr\":[{\"text\":\"пробежать милю\"}]}]},{\"text\":\"управлять\",\"pos\":\"глагол\",\"asp\":\"несов\",\"syn\":[{\"text\":\"руководить\",\"pos\":\"глагол\",\"asp\":\"несов\"}],\"mean\":[{\"text\":\"manage\"}],\"ex\":[{\"text\":\"run the ship\",\"tr\":[{\"text\":\"управлять кораблем\"}]},{\"text\":\"run the government\",\"tr\":[{\"text\":\"руководить правительством\"}]}]},{\"text\":\"идти\",\"pos\":\"глагол\",\"asp\":\"несов\",\"syn\":[{\"text\":\"ходить\",\"pos\":\"глагол\",\"asp\":\"несов\"},{\"text\":\"двигаться\",\"pos\":\"глагол\",\"asp\":\"несов\"},{\"text\":\"вращаться\",\"pos\":\"глагол\",\"asp\":\"несов\"}],\"mean\":[{\"text\":\"go\"},{\"text\":\"move\"},{\"text\":\"rotate\"}],\"ex\":[{\"text\":\"run counter\",\"tr\":[{\"text\":\"идти вразрез\"}]},{\"text\":\"run around\",\"tr\":[{\"text\":\"ходить вокруг\"}]},{\"text\":\"run smoothly\",\"tr\":[{\"text\":\"двигаться легко\"}]}]},{\"text\":\"баллотироваться\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"stand\"}],\"ex\":[{\"text\":\"run for office\",\"tr\":[{\"text\":\"баллотироваться на должность\"}]}]},{\"text\":\"вести\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"conduct\"}]},{\"text\":\"течь\",\"pos\":\"глагол\",\"asp\":\"несов\",\"syn\":[{\"text\":\"протекать\",\"pos\":\"глагол\",\"asp\":\"несов\"},{\"text\":\"литься\",\"pos\":\"глагол\",\"asp\":\"несов\"}],\"mean\":[{\"text\":\"flow\"}],\"ex\":[{\"text\":\"run concurrently\",\"tr\":[{\"text\":\"протекать одновременно\"}]}]},{\"text\":\"курсировать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"ply\"}]},{\"text\":\"запустить\",\"pos\":\"глагол\",\"asp\":\"сов\",\"syn\":[{\"text\":\"запускать\",\"pos\":\"глагол\",\"asp\":\"несов\"}],\"mean\":[{\"text\":\"start\"}],\"ex\":[{\"text\":\"run the application\",\"tr\":[{\"text\":\"запустить приложение\"}]},{\"text\":\"run simultaneously\",\"tr\":[{\"text\":\"запускать одновременно\"}]}]},{\"text\":\"убежать\",\"pos\":\"глагол\",\"asp\":\"сов\",\"syn\":[{\"text\":\"сбежать\",\"pos\":\"глагол\",\"asp\":\"сов\"}],\"mean\":[{\"text\":\"escape\"}]},{\"text\":\"участвовать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"participate\"}]},{\"text\":\"иметь\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"have\"}]},{\"text\":\"простираться\",\"pos\":\"глагол\",\"syn\":[{\"text\":\"достигать\",\"pos\":\"глагол\",\"asp\":\"несов\"}],\"mean\":[{\"text\":\"reach\"}]},{\"text\":\"подвергаться\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"be\"}]},{\"text\":\"кончаться\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"end\"}]},{\"text\":\"истекать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"expire\"}]},{\"text\":\"прокладывать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"lay\"}],\"ex\":[{\"text\":\"run routes\",\"tr\":[{\"text\":\"прокладывать маршруты\"}]}]},{\"text\":\"хватать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"suffice\"}]},{\"text\":\"гнать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"drive\"}]},{\"text\":\"лететь\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"fly\"}]},{\"text\":\"расплываться\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"spread\"}]},{\"text\":\"расти\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"grow\"}]},{\"text\":\"хозяйничать\",\"pos\":\"глагол\",\"asp\":\"несов\",\"mean\":[{\"text\":\"host\"}]}]},{\"text\":\"run\",\"pos\":\"noun\",\"ts\":\"rʌn\",\"tr\":[{\"text\":\"бег\",\"pos\":\"существительное\",\"gen\":\"м\",\"syn\":[{\"text\":\"пробежка\",\"pos\":\"существительное\",\"gen\":\"ж\"}],\"mean\":[{\"text\":\"running\"},{\"text\":\"jog\"}],\"ex\":[{\"text\":\"shuttle run\",\"tr\":[{\"text\":\"челночный бег\"}]},{\"text\":\"morning run\",\"tr\":[{\"text\":\"утренняя пробежка\"}]}]},{\"text\":\"работа\",\"pos\":\"существительное\",\"gen\":\"ж\",\"mean\":[{\"text\":\"work\"}],\"ex\":[{\"text\":\"motor run\",\"tr\":[{\"text\":\"работа двигателя\"}]}]},{\"text\":\"трасса\",\"pos\":\"существительное\",\"gen\":\"ж\",\"mean\":[{\"text\":\"route\"}],\"ex\":[{\"text\":\"ski run\",\"tr\":[{\"text\":\"лыжная трасса\"}]}]},{\"text\":\"прогон\",\"pos\":\"существительное\",\"gen\":\"м\",\"syn\":[{\"text\":\"запуск\",\"pos\":\"существительное\",\"gen\":\"м\"}],\"mean\":[{\"text\":\"girder\"},{\"text\":\"start\"}],\"ex\":[{\"text\":\"dry run\",\"tr\":[{\"text\":\"пробный прогон\"}]},{\"text\":\"trial run\",\"tr\":[{\"text\":\"пробный запуск\"}]}]},{\"text\":\"пробег\",\"pos\":\"существительное\",\"gen\":\"м\",\"mean\":[{\"text\":\"mileage\"}],\"ex\":[{\"text\":\"test run\",\"tr\":[{\"text\":\"испытательный пробег\"}]}]},{\"text\":\"вести\",\"pos\":\"существительное\",\"mean\":[{\"text\":\"lead\"}],\"ex\":[{\"text\":\"run business\",\"tr\":[{\"text\":\"вести дела\"}]}]},{\"text\":\"ход\",\"pos\":\"существительное\",\"gen\":\"м\",\"mean\":[{\"text\":\"move\"}]},{\"text\":\"выполнение\",\"pos\":\"существительное\",\"gen\":\"ср\",\"mean\":[{\"text\":\"execution\"}],\"ex\":[{\"text\":\"run time\",\"tr\":[{\"text\":\"время выполнения\"}]}]},{\"text\":\"тираж\",\"pos\":\"существительное\",\"gen\":\"м\",\"mean\":[{\"text\":\"circulation\"}],\"ex\":[{\"text\":\"total run\",\"tr\":[{\"text\":\"общий тираж\"}]}]},{\"text\":\"период\",\"pos\":\"существительное\",\"gen\":\"м\",\"mean\":[{\"text\":\"period\"}]},{\"text\":\"рейс\",\"pos\":\"существительное\",\"gen\":\"м\",\"mean\":[{\"text\":\"flight\"}]},{\"text\":\"серия\",\"pos\":\"существительное\",\"gen\":\"ж\",\"mean\":[{\"text\":\"series\"}]},{\"text\":\"показ\",\"pos\":\"существительное\",\"gen\":\"м\",\"mean\":[{\"text\":\"show\"}],\"ex\":[{\"text\":\"run movies\",\"tr\":[{\"text\":\"показы кинофильмов\"}]}]},{\"text\":\"партия\",\"pos\":\"существительное\",\"gen\":\"ж\",\"mean\":[{\"text\":\"batch\"}]},{\"text\":\"полоса\",\"pos\":\"существительное\",\"gen\":\"ж\",\"mean\":[{\"text\":\"strip\"}]}]}]}";

//    private final String jsonStr = "{\"total\":\"137\",\"infos\":[],\"errors\":[],\"warnings\":[],\"success\":true,\"rows\":[{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"prospekt Kirova 328, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728331\",\"dropWindows\":[{\"startTime\":1471651200000,\"endTime\":1471737540000,\"orderId\":\"2728331\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728331\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Novo-Vokzal'naya ulitsa 277, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728373\",\"dropWindows\":[{\"startTime\":1471651200000,\"endTime\":1471737540000,\"orderId\":\"2728373\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728373\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"FAILED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Leninskaya ulitsa 121, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728378\",\"dropWindows\":[{\"startTime\":1474588800000,\"endTime\":1474675140000,\"orderId\":\"2728378\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728378\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"COMMITTING\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Nevskaya ulitsa, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728379\",\"dropWindows\":[{\"startTime\":1483056000000,\"endTime\":1483142340000,\"orderId\":\"2728379\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728379\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"ulitsa Chelyuskintsev, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728390\",\"dropWindows\":[{\"startTime\":1471910400000,\"endTime\":1471996740000,\"orderId\":\"2728390\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728390\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"prospekt Karla Marksa 122, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728391\",\"dropWindows\":[{\"startTime\":1471910400000,\"endTime\":1471996740000,\"orderId\":\"2728391\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728391\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Novo-Sadovaya ulitsa 367, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728413\",\"dropWindows\":[{\"startTime\":1471996800000,\"endTime\":1472083140000,\"orderId\":\"2728413\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728413\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"prospekt Karla Marksa 122, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728420\",\"dropWindows\":[{\"startTime\":1471996800000,\"endTime\":1472083140000,\"orderId\":\"2728420\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728420\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"DELIVERY_ON_MOVE\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"prospekt Karla Marksa 122, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728423\",\"dropWindows\":[{\"startTime\":1472256000000,\"endTime\":1472342340000,\"orderId\":\"2728423\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728423\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"NEW\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"prospekt Karla Marksa 124, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728424\",\"dropWindows\":[{\"startTime\":1472601600000,\"endTime\":1472687940000,\"orderId\":\"2728424\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728424\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Moskovskoe shosse, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728433\",\"dropWindows\":[{\"startTime\":1472169600000,\"endTime\":1472255940000,\"orderId\":\"2728433\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728433\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"DELIVERY_ON_MOVE\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"ulitsa Chelyuskintsev, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728434\",\"dropWindows\":[{\"startTime\":1472083200000,\"endTime\":1472169540000,\"orderId\":\"2728434\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728434\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"COMMITTED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"prospekt Karla Marksa 122, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2728435\",\"dropWindows\":[{\"startTime\":1472083200000,\"endTime\":1472169540000,\"orderId\":\"2728435\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728435\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"NEW\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Ф\"},\"isLocationChanged\":false,\"id\":\"2728436\",\"dropWindows\":[{\"startTime\":1472256000000,\"endTime\":1472342340000,\"orderId\":\"2728436\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2728436\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Kievskaya ulitsa 17, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2729310\",\"dropWindows\":[{\"startTime\":1472169600000,\"endTime\":1472255940000,\"orderId\":\"2729310\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2729310\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"CLOSED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Kievskaya ulitsa, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2729316\",\"dropWindows\":[{\"startTime\":1472428800000,\"endTime\":1472515140000,\"orderId\":\"2729316\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2729316\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"DELIVERY_ON_MOVE\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"prospekt Karla Marksa 122, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2729317\",\"dropWindows\":[{\"startTime\":1472428800000,\"endTime\":1472515140000,\"orderId\":\"2729317\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2729317\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"COMMITTED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Tushinskaya ulitsa 45, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2734929\",\"dropWindows\":[{\"startTime\":1473292800000,\"endTime\":1473379140000,\"orderId\":\"2734929\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734929\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"COMMITTED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Tverskaya ulitsa 136, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2734930\",\"dropWindows\":[{\"startTime\":1473292800000,\"endTime\":1473379140000,\"orderId\":\"2734930\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734930\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Novo-Sadovaya ulitsa 379, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2734931\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734931\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734931\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"12\"},\"isLocationChanged\":false,\"id\":\"2734932\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734932\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734932\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"12\"},\"isLocationChanged\":false,\"id\":\"2734933\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734933\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734933\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"15\"},\"isLocationChanged\":false,\"id\":\"2734934\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734934\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734934\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"12\"},\"isLocationChanged\":false,\"id\":\"2734935\",\"dropWindows\":[{\"startTime\":1473386400000,\"endTime\":1473451200000,\"orderId\":\"2734935\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734935\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"15\"},\"isLocationChanged\":false,\"id\":\"2734936\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734936\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734936\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"15\"},\"isLocationChanged\":false,\"id\":\"2734937\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734937\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734937\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"15\"},\"isLocationChanged\":false,\"id\":\"2734938\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734938\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734938\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"ulitsa Michurina 185, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2734939\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734939\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734939\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"ALLOCATED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"Tashkentskaya ulitsa 248, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2734940\",\"dropWindows\":[{\"startTime\":1473379200000,\"endTime\":1473465540000,\"orderId\":\"2734940\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2734940\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true},{\"status\":\"COMMITTED\",\"dropWindowsChanged\":false,\"destination\":{\"corrected\":false,\"name\":\"ulitsa Dzerzhinskogo 26, Samara, Volga Federal District, 443000, RUS\"},\"isLocationChanged\":false,\"id\":\"2737017\",\"dropWindows\":[{\"startTime\":1475200800000,\"endTime\":1475279940000,\"orderId\":\"2737017\"}],\"attributeValues\":{\"is_return_packages\":\"false\"},\"isUnionOrders\":false,\"source\":{\"corrected\":false,\"name\":\"Magenta\"},\"name\":\"mID2737017\",\"currentClientTimeZone\":{\"lastRuleInstance\":{\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"rawOffset\":\"0\",\"DSTSavings\":\"3600000\",\"dirty\":false,\"ID\":\"Europe/London\",\"displayName\":\"Greenwich Mean Time\"},\"nonPersistedValues\":{},\"isValid\":true}],\"ordersCount\":\"0\"}";

    public JSONView(Context context) {
        super(context);
    }

    public JSONView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JSONView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JSONView init(String json) {
        try {
            draw(this, new JSONObject(json), 40);
        } catch (JSONException e) {
            try {
                draw(this, new JSONArray(json), 40);
            } catch (JSONException ex) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
        return this;
    }

    private void draw(ViewGroup parent, JSONArray array, int colorInt) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(VERTICAL);
            l.setPadding(24, 12, 24, 12);
            l.setBackgroundColor(Color.argb(colorInt, colorInt, colorInt, colorInt));
            parent.addView(l);
            draw(l, array.getJSONObject(i), colorInt + 30);
        }
    }

    private void draw(ViewGroup parent, JSONObject object, int colorInt) throws JSONException {
        if (object == null) {
            return;
        }
        Iterator iterator = object.keys();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            if (key.toString().equalsIgnoreCase("GED")) {
                continue;
            }
            LinearLayout layout = new LinearLayout(getContext());
            layout.setBackgroundResource(R.drawable.border);
            layout.setOrientation(VERTICAL);
            layout.setPadding(24, 12, 24, 12);
            parent.addView(layout);
            Object o = object.get(key.toString());
            if (o instanceof JSONObject) {
                draw(layout, (JSONObject) o, colorInt + 30);
            } else if (o instanceof JSONArray) {
                JSONArray a = (JSONArray) o;
                for (int i = 0; i < a.length(); i++) {
                    LinearLayout l = new LinearLayout(getContext());
                    l.setOrientation(VERTICAL);
                    l.setPadding(24, 12, 24, 12);
                    l.setBackgroundColor(Color.argb(colorInt, colorInt, colorInt, colorInt));
                    layout.addView(l);
                    draw(l, a.getJSONObject(i), colorInt + 30);
                }
            } else {
                addView(layout, key.toString(), o.toString());
            }
        }
    }

    private void addView(ViewGroup parent, String key, String text) {
        View view = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.view_item_json, null);
        ((TextView) view.findViewById(R.id.key)).setText(key);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        parent.addView(view);
    }
}