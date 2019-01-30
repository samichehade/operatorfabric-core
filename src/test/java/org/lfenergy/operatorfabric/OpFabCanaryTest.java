package org.lfenergy.operatorfabric;

import org.fluentlenium.adapter.junit.jupiter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.os.ExecutableFinder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

@Wait
public class OpFabCanaryTest extends FluentTest {

    @Override
    public WebDriver newWebDriver() {

        FirefoxOptions options = new FirefoxOptions();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);

        return new FirefoxDriver(options);
    }
    @Disabled
    @Test
    public void goThroughSPAForACanaryTest() {
        goTo("http://localhost:2002/ui/");

        FluentList<FluentWebElement> identifierInput = $("input[formcontrolname=\"identifier\"");
//        identifierInput
                $("input").get(0).fill().with("rte-operator");

        FluentList<FluentWebElement> passwordInput = $("input[formcontrolname=\"password\"");
        passwordInput.fill().with("test");

        FluentList<FluentWebElement> login= $("button", withText("Login"));
        login.submit();
        assertThat(window().title()).contains("Operator Fabric");

        FluentList<FluentWebElement> cardResumes = $("of-card");
        FluentWebElement secondCard = $(".row").index(2);
        FluentList<FluentWebElement> ofCardDetails = $("of-card-details");
        assertCardDetailsAreOk(cardResumes, secondCard, ofCardDetails);

        $(".nav-link",withText("Archives")).click();
        assertThat($("html body of-root of-archives p").textContents()).contains(" archives works!"+System.lineSeparator());

        $(".nav-link",withText("Flux de cartes")).click();
        assertCardDetailsAreOk(cardResumes, secondCard, ofCardDetails);

        $("html body of-root of-navbar nav.navbar.navbar-expand-lg.fixed-top.navbar-dark.bg-dark div#navbarContent.collapse.navbar-collapse ul.navbar-nav.mr-auto li form.ng-untouched.ng-pristine.ng-valid button.btn.btn-link.btn-logout").click();
        assertThat(identifierInput).isNotNull();
        assertThat(passwordInput).isNotNull();
    }

    private void assertCardDetailsAreOk(FluentList<FluentWebElement> cardResumes, FluentWebElement secondCard, FluentList<FluentWebElement> ofCardDetails) {
        assertThat(cardResumes).hasSize(5);
        secondCard.click();
        assertThat(ofCardDetails).isNotNull();
        assertThat(ofCardDetails).hasSize(1);
    }

}
