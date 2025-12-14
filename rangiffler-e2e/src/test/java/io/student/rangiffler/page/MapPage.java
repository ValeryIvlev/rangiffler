package io.student.rangiffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MapPage {

    private final SelenideElement map = $("figure.worldmap__figure-container");

    public MapPage shouldBeVisibleMap(){
        map.shouldBe(visible);
        return this;
    }

    public MapPage shouldNotVisibleMap(){
        map.shouldNotBe(visible);
        return this;
    }
}
