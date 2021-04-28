package app;

import extension.global.LwjglWindow;
import render.Renderer;

public class App {
    public static void main(String[] args) {
        new LwjglWindow(new Renderer());
        /* Zadání:
         * Naprogramovat tří rozměrnou scénu, ve které se bude zobrazovat
         * předem vygenerovaný labyrint. Celé zadání je ozvláštněno o sbírání bodů, animované skóre,
         * timer s počítáním času (strávený v daném bludišti) a finální/konečný debug mód.
         *
         * Zpracování:
         * Do svého projektu jsem zaimplementoval Jarníkův algoritmus pro tvorbu labyrintů (viz komentáře ve třídě MazeGenerator)
         * Výstup tohoto generování probíhal do dvou rozměrného pole, které následně pomocí třídy MazeProcessor procházím a tvořím z něj objekty.
         * V této třídě taktéž generuji skóre.
         *
         * Krátký popis ostatních custom tříd:
         * models - GameObject - reprezentuje herní objekty (vytvořil jsem tuto třídu kvůli sjednocení vlastností - ostatní objekty z models z ní dědí)
         * models - Floor - představuje podlahu celého labyrintu.
         * models - Wall - jak název napovídá jedná se o zeď. Tvořena z osmi trojuhelníků (záleží na výšce)
         * models - Score - představuje skóre které lze vzít.
         * models - FloorPoints - pod touto třídou se schovávají modely pro startovací a konečný bod
         *
         * utils - MazeGenerator - generuje labyrint do 2D pole pomocí Jarníkova algoritmu
         * utils - MazeProcessor - z vygenerovaného pole vytváří skóre a zdi
         * utils - ShrinkScoreUtil - třída určená pro animaci skóre (Implementaci v podobě třídy jsem zvolil především kvůli Threadu)
         *
         * render - Renderer - zprostředkovává funkčnost všech ostatních tříd - vykresluje vše a spravuje interakce.
         *
         * Závěr / osobní poznatky:
         * Tímto projektem se mi povedlo nahlédnout do problematiky týkající se optimalizace algoritmů.
         * Několika řádky bylo možné bez řádné optimalizace naprosto snížit FPS mého programu, což mě navedlo na "optimalizační misi".
         * Taktéž jsem si tímto projektem odnesl nové zkušenosti ohledně počítačové grafiky, ale i programování větších projektů.
         * K výtkám z mé strany patří především opomenutí tvorby struktury projektu. Sám musím uznat, že jsem v zápalu tomuto faktoru nevěnoval dostatečný čas.
         * Věřím, že na této části by se dalo určitě ještě zapracovat - avšak tuto vadu snad vynahradí kompletně/specificky okomentovaný kód.
         *
         * Projekt pro předmět PGRF2 zpracoval Zdeněk Hejzlar. Poslední úpravy: 28.04.2021
         * */
    }
}
