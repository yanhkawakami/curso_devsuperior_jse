package entities.tests;

import entities.Financing;
import entities.tests.factory.FinancingFactory;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

public class FinancingTests {

    @Test
    public void constructorShouldCreateObjectWhenDataAreValid(){
        double totalAmount = 100000.0;
        double income = 2000.0;
        int months = 80;
        Assertions.assertAll(() -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
        });
    }

    @Test
    public void constructorShouldThrowExceptionWhenDataAreNotValid(){
        double totalAmount = 100000.0;
        double income = 2000.0;
        int months = 20;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
        });
    }

    @Test
    public void totalAmountShouldBeUpdatedWhenDataAreValid() {
        double totalAmount = 80000.0;
        double income = 2000.0;
        int months = 80;
        double newTotalAmount = 100000.0;
        Assertions.assertAll(() -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
            fin.setTotalAmount(newTotalAmount);
        });
    }

    @Test
    public void setTotalAmountShouldThrowExceptionWhenDataAreNotValid() {
        double totalAmount = 80000.0;
        double income = 2000.0;
        int months = 80;
        double newTotalAmount = 1000000.0;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
            fin.setTotalAmount(newTotalAmount);
        });
    }

    @Test
    public void incomeShouldBeUpdatedWhenDataAreValid() {
        double totalAmount = 100000.0;
        double income = 2000.0;
        int months = 80;
        double newIncome = 3000.0;
        Assertions.assertAll(() -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
            fin.setIncome(newIncome);
        });
    }

    @Test
    public void setIncomeShouldThrowExceptionWhenDataAreNotValid() {
        double totalAmount = 80000.0;
        double income = 2000.0;
        int months = 80;
        double newIncome = 1000.0;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
            fin.setIncome(newIncome);
        });
    }

    @Test
    public void monthsShouldBeUpdatedWhenDataAreValid() {
        double totalAmount = 100000.0;
        double income = 2000.0;
        int months = 80;
        int newMonths = 100;
        Assertions.assertAll(() -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
            fin.setMonths(newMonths);
        });
    }

    @Test
    public void setMonthsShouldThrowExceptionWhenDataAreNotValid() {
        double totalAmount = 100000.0;
        double income = 2000.0;
        int months = 80;
        int newMonths = 20;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);
            fin.setMonths(newMonths);
        });
    }

    @Test
    public void entryShouldBeCalculatedCorrectly(){
        double totalAmount = 100000.0;
        double income = 2000.0;
        int months = 80;

        double expectedValue = 20000.0;

        Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);

        assert fin.entry().equals(expectedValue);
    }

    @Test
    public void quotaShouldBeCalculatedCorrectly(){
        double totalAmount = 100000.0;
        double income = 2000.0;
        int months = 80;

        double expectedValue = 1000.0;

        Financing fin = FinancingFactory.createFinancing(totalAmount, income, months);

        assert fin.quota().equals(expectedValue);
    }



}
