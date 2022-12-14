package ru.netology.web.test;

import com.codeborne.selenide.Condition;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoneyTransferTest {

    @BeforeEach
    void setUpForAllTests() {
      open("http://localhost:9999");
      var loginPage = new LoginPage();
      var authInfo = DataHelper.getAuthInfo();
      var verificationPage = loginPage.validLogin(authInfo);
      var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
      verificationPage.validVerify(verificationCode);
    }

    @Test
    @Order(1)
    void shouldTransferMoneyFromSecondCardToFirstCard() {
        val dashboardPage = new DashboardPage();
        val amount = 2000;
        val expectedBalanceOfFirstCard = dashboardPage.getCurrentBalanceOfFirstCard();
        val expectedBalanceOfSecondCard = dashboardPage.getCurrentBalanceOfSecondCard();
        val transferPage = dashboardPage.transferToFirstCard();
        val transferInfo = getSecondCardNumber();
        transferPage.moneyTransfer(transferInfo, amount);
        val balanceOfFirstCard = getBalanceIfIncrease(expectedBalanceOfFirstCard, amount);
        val balanceOfSecondCard = getBalanceIfDecrease(expectedBalanceOfSecondCard, amount);
        val finalBalanceOfFirstCard = dashboardPage.getCurrentBalanceOfFirstCard();
        val finalBalanceOfSecondCard = dashboardPage.getCurrentBalanceOfSecondCard();
        assertEquals(balanceOfFirstCard, finalBalanceOfFirstCard);
        assertEquals(balanceOfSecondCard, finalBalanceOfSecondCard);
    }

    @Test
    @Order(2)
    void shouldTransferMoneyFromFirstCardToSecondCard() {
        val dashboardPage = new DashboardPage();
        val amount = 2500;
        val expectedBalanceOfSecondCard = dashboardPage.getCurrentBalanceOfSecondCard();
        val expectedBalanceOfFirstCard = dashboardPage.getCurrentBalanceOfFirstCard();
        val transferPage = dashboardPage.transferToSecondCard();
        val transferInfo = getFirstCardNumber();
        transferPage.moneyTransfer(transferInfo, amount);
        val balanceOfSecondCard = getBalanceIfIncrease(expectedBalanceOfSecondCard, amount);
        val balanceOfFirstCard = getBalanceIfDecrease(expectedBalanceOfFirstCard, amount);
        assertEquals(balanceOfSecondCard, balanceOfSecondCard);
        assertEquals(balanceOfFirstCard, balanceOfFirstCard);
    }

    @Test
    @Order(3)
    void shouldBeErrorWhenCardFieldIsEmpty() {
        val dashboardPage = new DashboardPage();
        val amount = 1000;
        val transferPage = dashboardPage.transferToFirstCard();
        val transferInfo = getEmptyCardNumber();
        transferPage.moneyTransfer(transferInfo, amount);
        transferPage.invalidMoneyTransfer();
    }

    @Test
    @Order(4)
    void shouldBeErrorWhenCardNumberIsWrong() {
        val dashboardPage = new DashboardPage();
        val amount = 1000;
        val transferPage = dashboardPage.transferToFirstCard();
        val transferInfo = getIncorrectCardNumber();
        transferPage.moneyTransfer(transferInfo, amount);
        transferPage.invalidMoneyTransfer();
    }

    @Test
    @Order(5)
    void shouldTransferNothingWhenAmountIsNull() {
        val dashboardPage = new DashboardPage();
        val amount = 0;
        val expectedBalanceOfSecondCard = dashboardPage.getCurrentBalanceOfSecondCard();
        val expectedBalanceOfFirstCard = dashboardPage.getCurrentBalanceOfFirstCard();
        val transferPage = dashboardPage.transferToSecondCard();
        val transferInfo = getFirstCardNumber();
        transferPage.moneyTransfer(transferInfo, amount);
        val balanceOfSecondCard = getBalanceIfIncrease(expectedBalanceOfSecondCard, amount);
        val balanceOfFirstCard = getBalanceIfDecrease(expectedBalanceOfFirstCard, amount);
        val finalBalanceOfSecondCard = dashboardPage.getCurrentBalanceOfSecondCard();
        val finalBalanceOfFirstCard = dashboardPage.getCurrentBalanceOfFirstCard();
        assertEquals(balanceOfSecondCard, finalBalanceOfSecondCard);
        assertEquals(balanceOfFirstCard, finalBalanceOfFirstCard);
    }

    @Test
    @Order(6)
    void shouldBeErrorWhenNotEnoughMoneyForTransfer() {
        val dashboardPage = new DashboardPage();
        val amount = dashboardPage.getCurrentBalanceOfSecondCard() + 15000;
        val transferPage = dashboardPage.transferToFirstCard();
        val transferInfo = getSecondCardNumber();
        transferPage.moneyTransfer(transferInfo, amount);
        transferPage.invalidMoneyTransfer();
    }
}

