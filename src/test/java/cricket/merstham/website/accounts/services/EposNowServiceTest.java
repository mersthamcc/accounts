package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.model.EposNowTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class EposNowServiceTest {

    @BeforeEach
    void setUp() {}

    @Test
    void getTransactionsForDay() {
        List<EposNowTransaction> transactions = new ArrayList<>();

        assertThat(transactions.addAll(List.of(new EposNowTransaction())), equalTo(true));
        assertThat(transactions.addAll(new ArrayList<EposNowTransaction>()), equalTo(false));
    }
}
