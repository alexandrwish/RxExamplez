package com.magenta.rx.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            // Вводим число x
            System.out.print("Введите степень при е: ");
            String enter = in.readLine();
            int stepen = Integer.parseInt(enter);
            // Создаем поток
            myThread t = new myThread(stepen);
            // Упакуем его в объект класса Thread
            Thread t1 = new Thread(t);
            // Старт!
            t1.start();

            while (true) {
                String enter1 = in.readLine();
                if (enter1.equals("e")) {
                    // Остановка потока
                    t1.interrupt();
                    t1.join();
                    return;
                }
                // Иначе - это строка - вывод результата
                System.out.println("Текущий результат: " + t.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int s = 0;
        int e = 10;
        int j = 1;
        for (int i = s; i < e; i += j) {

        }
    }
}

class myThread implements Runnable {

    private double result; // текущий результат
    private int stepen; // степень при е

    // Конструктор потока
    public myThread(int stepen) {
        this.stepen = stepen;
        this.result = 0;
    }

    // Метод, который вызывается при старте потока
    public void run() {
        // начинаем с 0 члена
        try {
            exp(0);
        } catch (InterruptedException e) {
            return;
        }
    }

    private void exp(int count) throws InterruptedException {
        // Прибавим следующий член ряда
        result += Math.pow(stepen, count) / factorial(count);
        // перерыв - 1 секунда
        Thread.sleep(1000);
        // Следующая итерация
        exp(++count);
    }

    // Вычисление факториала - возможно есть альтернативные java средства?
    private int factorial(int number) {
        if (number == 0)
            number = 1;
        else
            number *= factorial(--number);

        return number;
    }

    public synchronized double getResult() {
        return result;
    }
}
