package com.rachelbock;

import com.rachelbock.resources.*;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ClamberServerApplication extends Application<ClamberServerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ClamberServerApplication().run(args);
    }

    @Override
    public String getName() {
        return "Clamber Server";
    }

    @Override
    public void initialize(final Bootstrap<ClamberServerConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/assets", "index.html"));

    }

    @Override
    public void run(final ClamberServerConfiguration configuration,
                    final Environment environment) {
        environment.jersey().register(new WallsResource());
        environment.jersey().register(new UserResource());
        environment.jersey().register(new ProjectResource());
        environment.jersey().register(new CompletedResource());
        environment.jersey().register(new CommentsResource());
    }

}
