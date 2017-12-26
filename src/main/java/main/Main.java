package main;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.awt.*;
import java.io.IOException;
import java.net.URI;


public class Main {
    private static Server server = null;

    private static void SetUp() throws Exception {
        server = new Server(80);
        WebAppContext ctx = new WebAppContext();
        ctx.setResourceBase("src/main/webapp");
        ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/[^/]*jstl.jar$");
        org.eclipse.jetty.webapp.Configuration.ClassList classList = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classList.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classList.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");
        server.setHandler(ctx);
        server.start();
    }

    private static void TearDown() throws Exception {
        server.join();
    }

    private static void browse(URI uri) throws IOException {
        Desktop.getDesktop().browse(uri);
    }

    private static void getCodeVk() throws IOException {
        browse(URI.create("https://oauth.vk.com/authorize/?client_id=6246163&display=popup&redirect_uri=http://localhost/auth&response_type=code"));
    }

    public static void main(String[] args) throws Exception {
        SetUp();
        getCodeVk();
        TearDown();
    }
}
