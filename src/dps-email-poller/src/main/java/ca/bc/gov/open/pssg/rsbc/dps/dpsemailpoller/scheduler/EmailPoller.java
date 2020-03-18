package ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.scheduler;

import ca.bc.gov.open.pssg.rsbc.DpsMetadata;
import ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.email.DpsEmailException;
import ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.email.DpsMetadataMapper;
import ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.email.EmailService;
import ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.messaging.MessagingService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailPoller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EmailService emailService;
    private final DpsMetadataMapper dpsMetadataMapper;
    private final MessagingService messagingService;
    private final String tenant;

    public EmailPoller(
            EmailService emailService,
            DpsMetadataMapper dpsMetadataMapper,
            MessagingService messagingService,
            @Value("${dps.tenant}") String tenant) {
        this.emailService = emailService;
        this.dpsMetadataMapper = dpsMetadataMapper;
        this.messagingService = messagingService;
        this.tenant = tenant;
    }

    @Scheduled(cron = "${mailbox.interval}")
    public void pollForEmails() {

        logger.debug("perform poll for emails");

        try {

            List<EmailMessage> dpsEmails = emailService.getDpsInboxEmails();
            logger.info("successfully retrieved {} emails", dpsEmails.size());

            dpsEmails.forEach(item -> {

                emailService.moveToProcessingFolder(item);
                logger.info("successfully moved message to processing folder");

                DpsMetadata metadata = dpsMetadataMapper.map(item, this.tenant);

                messagingService.sendMessage(metadata, this.tenant);
                logger.info("successfully send message to processing queue");
            });

        } catch (DpsEmailException e) {
            logger.error("exception while processing dps emails", e);
        }
    }

    /**
     * This Job remove junk email from the inbox and move them to the error folder.
     */
    @Scheduled(cron = "${mailbox.poller.cron}")
    public void junkRemoval() {

        logger.debug("perform poll for junk emails");

        try {
            List<EmailMessage> junkEmails = emailService.getDpsInboxJunkEmails();
            logger.info("successfully retrieved {} junk emails", junkEmails.size());

            junkEmails.forEach(item -> {

                emailService.moveToErrorFolder(item);
                logger.info("successfully moved message to errorHold folder");
            });

        } catch (DpsEmailException e) {
            logger.error("exception while cleaning junk emails", e);
        }
    }

}
