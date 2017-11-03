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

import org.iotivity.base.OcException;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.PayloadType;

/**
 * Light
 *
 * This class represents a light resource
 */
public class Light extends Resource {
    static private final String RES_TYPE = "oic.d.light";
    
    static private final String LINKS_KEY = "links";

    private Links resourceLinks;

    private LightConfig lightConfigRes;
    private Switch switchRes;
    private Brightness brightnessRes;

    public Light(String name, String uuid, boolean powerOn, int brightness, LightPanel ui) {
        super("/ocf/light/" + uuid, RES_TYPE, OcPlatform.DEFAULT_INTERFACE);

        lightConfigRes = new LightConfig(name, uuid, this);
        lightConfigRes.addObserver(ui);
        OcfLightDevice.msg("Created config resource: " + lightConfigRes);

        switchRes = new Switch(uuid);
        switchRes.setValue(powerOn);
        switchRes.addObserver(ui);
        ui.addObserver(switchRes);
        OcfLightDevice.msg("Created switch resource: " + switchRes);

        brightnessRes = new Brightness(uuid);
        brightnessRes.setBrightness(brightness);
        brightnessRes.addObserver(ui);
        ui.addObserver(brightnessRes);
        OcfLightDevice.msg("Created brightness resource: " + brightnessRes);

        try {
            OcPlatform.setPropertyValue(PayloadType.PLATFORM.getValue(), "mnmn", "Intel");

            setDeviceName(name);
            OcPlatform.setPropertyValue(PayloadType.DEVICE.getValue(), "icv", "ocf.1.0.0");
            OcPlatform.setPropertyValue(PayloadType.DEVICE.getValue(), "dmv", "ocf.res.1.0.0,ocf.sh.1.0.0");

        } catch (OcException e) {
            OcfLightDevice.msgError("Failed to create properties for " + getResourceUri());
            e.printStackTrace();
        }

        Link[] links = new Link[3];
        links[0] = new Link(lightConfigRes.getResourceUri(), new String[] {LightConfig.RES_TYPE});
        links[1] = new Link(switchRes.getResourceUri(), new String[] {Switch.RES_TYPE});
        links[2] = new Link(brightnessRes.getResourceUri(), new String[] {Brightness.RES_TYPE});
        resourceLinks = new Links(links);

        try {
            OcPlatform.bindInterfaceToResource(getResourceHandle(), OcPlatform.LINK_INTERFACE);

        } catch (OcException e) {
            OcfLightDevice.msgError("Failed to bind link interface for " + getResourceUri());
            e.printStackTrace();
        }

        OcfLightDevice.msg("Created light resource: " + this);
    }

    public void setDeviceName(String name) {
        try {
            OcPlatform.setPropertyValue(PayloadType.DEVICE.getValue(), "n", name);

        } catch (OcException e) {
            OcfLightDevice.msgError("Failed to set device name to " + name);
            e.printStackTrace();
        }
    }

    public void setOcRepresentation(OcRepresentation rep) {
        try {
            if (rep.hasAttribute(LINKS_KEY)) {
                OcRepresentation[] links = rep.getValue(LINKS_KEY);
                resourceLinks.setOcRepresentation(links);
            }
        } catch (OcException e) {
            OcfLightDevice.msgError(e.toString());
            OcfLightDevice.msgError("Failed to get representation values");
        }
    }

    public OcRepresentation getOcRepresentation() {
        OcRepresentation rep = new OcRepresentation();
        try {
            OcRepresentation[] links = resourceLinks.getOcRepresentation();
            rep.setValue(LINKS_KEY, links);
        } catch (OcException e) {
            OcfLightDevice.msgError(e.toString());
            OcfLightDevice.msgError("Failed to set representation values");
        }
        return rep;
    }

    @Override
    public String toString() {
        return "[" + super.toString() + ", " + resourceLinks + "]";
    }
}
