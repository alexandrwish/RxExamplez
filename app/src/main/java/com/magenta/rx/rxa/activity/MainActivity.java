package com.magenta.rx.rxa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.magenta.rx.rxa.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends Activity {

    @BindView(R.id.list)
    ListView list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Google Map v2", "Yandex translate + Retrofit", "Google Map v2 + Google Location Service", "Yandex dictionary + Retrofit"}));
    }

    @OnItemClick(value = R.id.list)
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                startActivity(new Intent(MainActivity.this, MapActivity.class)); // простой пример MVP
                break;
            }
            case 1: {
                startActivity(new Intent(MainActivity.this, RetrofitActivity.class)); // пример асинхронной работы
                break;
            }
            case 2: {
                startActivity(new Intent(MainActivity.this, ServiceActivity.class)); // пример работы с сервисами (какой-то гибридный получился)
                break;
            }
            case 3: {
                startActivity(new Intent(MainActivity.this, DictionaryActivity.class)); // пример работы с БД
                break;
            }
            case 4: {
                // пример разграничения доступа к ресурсам
            }
            default: {

            }
        }
    }
}