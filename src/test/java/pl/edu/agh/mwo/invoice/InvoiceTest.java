package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testInvoiceHasNumberGreaterThan0() {
        int number = invoice.getNumber();
        Assert.assertThat(number, Matchers.greaterThan(0));
    }

    @Test
    public void testTwoInvoicesHaveDifferentNumbers() {
        int number1 = new Invoice().getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertNotEquals(number1, number2);
    }
    @Test
    public void testInvoiceDoesNotChangeItsNumber() {
        Assert.assertEquals(invoice.getNumber(), invoice.getNumber());
    }

    @Test
    public void testTheFirstInvoiceNumberIsLowerThanTheSecond() {
        int number1 = new Invoice().getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertThat(number1, Matchers.lessThan(number2));
    }

  @Test
    public void testPrintInvoiceAsString(){
        Assert.assertTrue ( new Invoice().printInvoice() instanceof String);
    }

    /*  @Test
    //ten test nie jest dobrym testem jednostkowym, bo nie przejdzie po zaimplementowniu całości,
    // ale pomoże mi zrobić krok w implementacji. W zasadzie ten test powinien być zakomentowny.
    public void testPrintHead(){
        String expectedResults = "Nr FAKTURY: " + invoice.getNumber() + "\n";
        Assert.assertEquals(expectedResults, invoice.printInvoice());
    }

    @Test  //j.w.
    public void testListOfProducts() {
        Invoice invoice = new Invoice();
        String expectedResults = "Owoce Maslanka Wino";
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")), 1);
        invoice.addProduct(new TaxFreeProduct("Maslanka", new BigDecimal("100")), 1);
        invoice.addProduct(new TaxFreeProduct("Wino", new BigDecimal("10")), 1);
        Assert.assertEquals(expectedResults, invoice.printInvoice());
    }

    @Test //j.w.
    public void testPrintInvoiceFooter() {
        Invoice invoice = new Invoice();
        String expectedResults = "Liczba pozycji: 3";
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")), 1);
        invoice.addProduct(new TaxFreeProduct("Maslanka", new BigDecimal("100")), 1);
        invoice.addProduct(new TaxFreeProduct("Wino", new BigDecimal("10")), 1);
        Assert.assertEquals(expectedResults, invoice.printInvoice());
    }

    @Test // j.w.
    public void testListOfProductsWithPrice() {
        Invoice invoice = new Invoice();
        String expectedResults = "Maslanka cena: 100 zł Owoce cena: 200 zł Wino cena: 10 zł ";

        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")), 1);
        invoice.addProduct(new TaxFreeProduct("Maslanka", new BigDecimal("100")), 1);
        invoice.addProduct(new TaxFreeProduct("Wino", new BigDecimal("10")), 1);
        Assert.assertEquals(expectedResults, invoice.printInvoice());
    }}*/

    @Test
    public void testPrintInvoiceTotal() {
        Invoice invoice = new Invoice();
        String expectedResults = "Nr FAKTURY: " + invoice.getNumber() + "\n" + "Maslanka szt: 3, cena/szt: 200 zł" + "\n" + "Owoce szt: 5, cena/szt: 100 zł" + "\n" + "Wino szt: 8, cena/szt: 10 zł" + "\n" + "Liczba pozycji: 3";

        invoice.addProduct(new TaxFreeProduct("Maslanka", new BigDecimal("200")), 3);
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("100")), 5);
        invoice.addProduct(new TaxFreeProduct("Wino", new BigDecimal("10")), 8);
        Assert.assertEquals(expectedResults, invoice.printInvoice());
    }



}
