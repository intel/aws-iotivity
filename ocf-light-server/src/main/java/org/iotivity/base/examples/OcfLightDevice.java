/*
 *******************************************************************
 *
 * Copyright 2017 Intel Corporation.
 *
 *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 */

package org.iotivity.base.examples;

import org.iotivity.base.ModeType;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * OcfLightDevice
 */
public class OcfLightDevice {

    static Light light;

    public static void main(String args[]) throws IOException, InterruptedException {

        String name = null;
        boolean powerOn = true;
        int brightness = 100;

        if (args.length > 0) {
            name = args[0];
        }

        if (args.length > 1) {
            String arg = args[1];
            powerOn = arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("on") || arg.equalsIgnoreCase("yes") || arg.equals("1");
        }

        if (args.length > 2) {
            try {
                brightness = Integer.valueOf(args[2]);
            } catch (NumberFormatException e) {
                msg("Brightness must be an integer in the range (0, 100), using default 100.");
            }

            brightness = Math.max(0, brightness);
            brightness = Math.min(100, brightness);
        }

        if (name == null || name.isEmpty()) {
            name = "Light-" + (System.currentTimeMillis() % 10000);
        }

        PlatformConfig platformConfig = new PlatformConfig(ServiceType.IN_PROC, ModeType.SERVER, "0.0.0.0", 0,
                QualityOfService.LOW);

        OcPlatform.Configure(platformConfig);
        msg("Platform configured");
        
        String uuid = UUID.randomUUID().toString();

        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                msg("Shutdown");
                e.getWindow().dispose();
                System.exit(0);
            }
        });

        frame.setResizable(false);
        frame.setLayout(new FlowLayout());

        LightPanel lightPanel = new LightPanel(powerOn, brightness);
        light = new Light(name, uuid, powerOn, brightness, lightPanel);

        frame.setContentPane(lightPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            msgError(e.toString());
        }
    }

    public static void msg(final String text) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date) + " " + text);
    }

    public static void msgError(final String text) {
        msg("[Error] " + text);
    }
}
