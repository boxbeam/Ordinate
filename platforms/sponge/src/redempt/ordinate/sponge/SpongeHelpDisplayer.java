package redempt.ordinate.sponge;

import org.spongepowered.api.service.permission.Subject;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.help.HelpEntry;
import redempt.ordinate.message.MessageFormatter;
import redempt.ordinate.message.MessageProvider;

public class SpongeHelpDisplayer implements HelpDisplayer<Subject> {

    private MessageFormatter<Subject> helpMessage;

    private String commandPrefix;

    public SpongeHelpDisplayer(String commandPrefix, MessageProvider<Subject> messages) {
        helpMessage = messages.getFormatter("helpFormat");
        this.commandPrefix = commandPrefix;
    }

    @Override
    public void display(Subject cause, HelpEntry<Subject> entry) {
        if (!entry.isVisibleTo(cause)) {
            return;
        }
        String parentPrefix = entry.getParentPrefix();
        if (parentPrefix.length() != 0) {
            parentPrefix += " ";
        }
        String fullUsage = commandPrefix + parentPrefix + entry.getUsage();
        String description = entry.getDescription();
        description = description == null ? "" : description;
        helpMessage.format(cause, fullUsage, description).send(cause);
    }
}
