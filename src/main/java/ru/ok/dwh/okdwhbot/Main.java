package ru.ok.dwh.okdwhbot;

import chat.tamtam.bot.exceptions.TamTamBotException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class Main {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Options OPTIONS = new Options();

    public static void main(String[] args) {
        BasicConfigurator.configure();
        System.out.println("started");
        OptionSet optionSet;
        try {
            optionSet = OPTIONS.parse(args);
        } catch (OptionException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        String accessToken = OPTIONS.accessToken.value(optionSet);
        String url = OPTIONS.url.value(optionSet);
        String user = OPTIONS.user.value(optionSet);
        String password = OPTIONS.password.value(optionSet);
        DwhBot bot = new DwhBot(accessToken, url, user, password);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping bot…");
            bot.stop();
        }));

        try {
            bot.start();
        } catch (TamTamBotException e) {
            System.err.println("Failed to start bot: " + e.getMessage());
            System.exit(1);
        }
    }

    private static class Options extends OptionParser {
        OptionSpec<String> accessToken = accepts("token").withRequiredArg().required().ofType(String.class);
        OptionSpec<String> url = accepts("url").withRequiredArg().required().ofType(String.class);
        OptionSpec<String> user = accepts("user").withRequiredArg().required().ofType(String.class);
        OptionSpec<String> password = accepts("password").withRequiredArg().required().ofType(String.class);
    }
}