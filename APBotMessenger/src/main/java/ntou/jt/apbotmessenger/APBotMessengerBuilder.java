package ntou.jt.apbotmessenger;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class APBotMessengerBuilder extends Builder implements SimpleBuildStep {

    private final String roomNumber;

    @DataBoundConstructor
    public APBotMessengerBuilder(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getroomNumber() {
        return roomNumber;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Hello, " + roomNumber + "!");
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckroomNumber(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.APBotMessengerBuilder_DescriptorImpl_errors_missingroomNumber());
            if (value.length() < 4)
                return FormValidation.warning(Messages.APBotMessengerBuilder_DescriptorImpl_warnings_tooShort());
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayroomNumber() {
            return Messages.APBotMessengerBuilder_DescriptorImpl_DisplayroomNumber();
        }

    }

}
