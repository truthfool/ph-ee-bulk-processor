package org.mifos.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonToCsvTest extends CamelTestSupport {

    @Produce(uri = "http://localhost:8080/batchtransaction")
    private ProducerTemplate producerTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testJsonToCsv() throws Exception{

        String json = "[{\"payer\":{\"partyIdInfo\":{\"partyIdType\":\"MSISDN\",\"partyIdentifier\":\"27710101999\"}},\"payee\":{\"partyIdInfo\":{\"partyIdType\":\"MSISDN\",\"partyIdentifier\":\"27710102999\"}},\"amount\":{\"amount\":22.5,\"currency\":\"USD\"},\"request_id\":\"f1e22fe3-9740-4fba-97b6-78f43bfa7f2f\",\"payment_mode\":\"mojaloop\",\"note\":\"TestPayment\"},{\"payer\":{\"partyIdInfo\":{\"partyIdType\":\"MSISDN\",\"partyIdentifier\":\"344136101999\"}},\"payee\":{\"partyIdInfo\":{\"partyIdType\":\"MSISDN\",\"partyIdentifier\":\"344136101999\"}},\"amount\":{\"amount\":21.5,\"currency\":\"USD\"},\"request_id\":\"ugfgr4u23-9894-sdvv-97b6-78f43bfa7f2f\",\"payment_mode\":\"mojaloop\"}]";

        String expectedCsv = "id,request_id,payment_mode,payer_identifier_type,payer_identifier,payee_identifier_type,payee_identifier,amount,currency,note\n" +
                "0,f1e22fe3-9740-4fba-97b6-78f43bfa7f2f,mojaloop,MSISDN,27710101999,MSISDN,27710102999,22.5,USD,Test Payment\n" +
                "1,ugfgr4u23-9894-sdvv-97b6-78f43bfa7f2f,mojaloop,MSISDN,344136101999,MSISDN,344136101999,21.5,USD,\n";

        // Send the JSON request
        Exchange exchange = producerTemplate.send("http://localhost:8080/batchtransaction", e -> {
            e.getIn().setBody(json);
            e.getIn().setHeader("Content-Type", "application/json");
            e.getIn().setHeader("Platform-TenantId", "gorilla");
            e.getIn().setHeader("X-CorrelationID", "12345678");
            e.getIn().setHeader("type", "raw");
        });

        String receivedCsv = exchange.getMessage().getBody(String.class);
        logger.info("Received CSV String : {}",receivedCsv);
        Assertions.assertEquals(expectedCsv, receivedCsv);

    }
}
