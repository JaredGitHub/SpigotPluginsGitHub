/*
 * Decompiled with CFR 0.151.
 */
package me.Jared.Guns;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import me.Jared.GunsPlugin;

public class WeaponReader {
    public GunsPlugin plugin;
    public boolean loaded = false;
    public File file;
    public String weaponType;
    public Gun ret;

    public WeaponReader(GunsPlugin plugin, File file, String string) {
        this.plugin = plugin;
        this.file = file;
        this.weaponType = string;
        this.ret = new Gun(file.getName());
        this.ret.setFilename(file.getName().toLowerCase());
        this.load();
    }

    private void computeData(String str) {
        try {
            if (str.indexOf("=") > 0) {
                String var = str.substring(0, str.indexOf("=")).toLowerCase();
                String val = str.substring(str.indexOf("=") + 1);
                if (var.equals("gunname")) {
                    this.ret.setName(val);
                }
                if (var.equals("guntype")) {
                    this.ret.setGunType(val);
                }
                if (var.equals("ammoamtneeded")) {
                    this.ret.setAmmoAmountNeeded(Integer.parseInt(val));
                }
                if (var.equals("reloadtime")) {
                    this.ret.setReloadTime(Integer.parseInt(val));
                }
                if (var.equals("gundamage")) {
                    this.ret.setGunDamage(Double.parseDouble(val));
                }
                if (var.equals("armorpenetration")) {
                    this.ret.setArmorPenetration(Integer.parseInt(val));
                }
                if (var.equals("ammotype")) {
                    this.ret.setAmmoType(val);
                }
                if (var.equals("roundsperburst")) {
                    this.ret.setRoundsPerBurst(Integer.parseInt(val));
                }
                if (var.equals("ray")) {
                    this.ret.setRay(Boolean.parseBoolean(val));
                }
                if (var.equals("maxdistance")) {
                    this.ret.setMaxDistance(Integer.parseInt(val));
                }
                if (var.equals("bulletsperclick")) {
                    this.ret.setBulletsPerClick(Integer.parseInt(val));
                }
                if (var.equals("bulletspeed")) {
                    this.ret.setBulletSpeed(Double.parseDouble(val));
                }
                if (var.equals("accuracy")) {
                    this.ret.setAccuracy(Double.parseDouble(val));
                }
                if (var.equals("accuracy_aimed")) {
                    this.ret.setAccuracyAimed(Double.parseDouble(val));
                }
                if (var.equals("exploderadius")) {
                    this.ret.setExplodeRadius(Double.parseDouble(val));
                }
                if (var.equals("gunvolume")) {
                    this.ret.setGunVolume(Double.parseDouble(val));
                }
                if (var.equals("fireradius")) {
                    this.ret.setFireRadius(Double.parseDouble(val));
                }
                if (var.equals("flashradius")) {
                    this.ret.setFlashRadius(Double.parseDouble(val));
                }
                if (var.equals("canheadshot")) {
                    this.ret.setCanHeadshot(Boolean.parseBoolean(val));
                }
                if (var.equals("knockback")) {
                    this.ret.setKnockback(Double.parseDouble(val));
                }
                if (var.equals("recoil")) {
                    this.ret.setRecoil(Double.parseDouble(val));
                }
                if (var.equals("bullettype")) {
                    this.ret.projType = val;
                }
                if (var.equals("hassmoketrail")) {
                    this.ret.setSmokeTrail(Boolean.parseBoolean(val));
                }
                if (var.equals("gunsound")) {
                    this.ret.addGunSounds(val);
                }
                if (var.equals("maxclipsize")) {
                    this.ret.maxClipSize = Integer.parseInt(val);
                }
                if (var.equals("hasclip")) {
                    this.ret.hasClip = Boolean.parseBoolean(val);
                }
                if (var.equals("reloadgunondrop")) {
                    this.ret.reloadGunOnDrop = Boolean.parseBoolean(val);
                }
                if (var.equals("bulletdelaytime")) {
                    this.ret.bulletDelayTime = Integer.parseInt(val);
                }
                if (var.equals("explosiondamage")) {
                    this.ret.setExplosionDamage(Integer.parseInt(val));
                }
                if (var.equals("timeuntilrelease")) {
                    this.ret.setReleaseTime(Integer.parseInt(val));
                }
                if (var.equals("reloadtype")) {
                    this.ret.reloadType = val;
                }
            }
        }
        catch (Exception e) {
            this.loaded = false;
        }
    }

    public void load() {
        this.loaded = true;
        ArrayList<String> file = new ArrayList<String>();
        try {
            String strLine;
            FileInputStream fstream = new FileInputStream(this.file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((strLine = br.readLine()) != null) {
                file.add(strLine);
            }
            br.close();
            in.close();
            fstream.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        int i = 0;
        while (i < file.size()) {
            this.computeData((String)file.get(i));
            ++i;
        }
    }
}

