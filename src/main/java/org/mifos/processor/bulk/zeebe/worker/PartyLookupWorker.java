package org.mifos.processor.bulk.zeebe.worker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.mifos.processor.bulk.zeebe.ZeebeVariables.PARTY_LOOKUP_FAILED;

@Component
public class PartyLookupWorker extends BaseWorker {

    @Override
    public void setup() {
        newWorker(Worker.PARTY_LOOKUP, (client, job) -> {
            logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
            Map<String, Object> variables = job.getVariablesAsMap();

            if (workerConfig.isPartyLookUpWorkerEnabled) {
                variables.put(PARTY_LOOKUP_FAILED, false);
            }

            client.newCompleteCommand(job.getKey()).variables(variables).send();
        });
    }

}
