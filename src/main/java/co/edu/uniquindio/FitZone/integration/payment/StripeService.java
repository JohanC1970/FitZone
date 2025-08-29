package co.edu.uniquindio.FitZone.integration.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key.secret}")
    private String secretKey;

    public PaymentIntent createPaymentIntent(Long amount, String currency, String description) throws StripeException {

        Stripe.apiKey =  secretKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("description", description);

        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription(description)
                .build();

        return  PaymentIntent.create(createParams);
    }

    public PaymentIntent getPaymentIntent(String paymentIntentId) throws StripeException {
        Stripe.apiKey =  secretKey;
        return PaymentIntent.retrieve(paymentIntentId);
    }

}
