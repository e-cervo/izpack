package com.izforge.izpack.installer.container.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.izforge.izpack.api.data.GUIPrefs;
import com.izforge.izpack.api.data.GUIPrefs.LookAndFeel;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.LookAndFeels;
import com.izforge.izpack.api.resource.Locales;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.core.data.DefaultVariables;
import com.izforge.izpack.gui.ButtonFactory;
import com.izforge.izpack.gui.IzPackKMetalTheme;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.util.Housekeeper;
import com.izforge.izpack.util.JavaVersion;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.PlatformModelMatcher;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provide installData for GUI :
 * Load install data with l&f and GUIPrefs
 */
@Singleton
public class GUIInstallDataProvider extends AbstractInstallDataProvider<GUIInstallData>
{
    private static final Logger logger = Logger.getLogger(GUIInstallDataProvider.class.getName());

    public static final String MODIFIER_USE_BUTTON_ICONS = "useButtonIcons";
    public static final String MODIFIER_USE_LABEL_ICONS = "useLabelIcons";
    public static final String MODIFIER_LABEL_FONT_SIZE = "labelFontSize";

    private static final Map<String, String> substanceVariants = new HashMap<String, String>();
    private static final Map<String, String> radianceVariants = new HashMap<>(48);
    private static final Map<String, String> looksVariants = new HashMap<String, String>();

    static
    {
        // For JDK <= 8
        substanceVariants.put("default", "org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel");

        substanceVariants.put("autumn", "org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel");
        substanceVariants.put("business", "org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel");
        substanceVariants.put("business-black", "org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel");
        substanceVariants.put("business-blue", "org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
        substanceVariants.put("creme", "org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel");
        substanceVariants.put("creme-coffee", "org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel");
        substanceVariants.put("dust", "org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
        substanceVariants.put("dust-coffee", "org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel");
        substanceVariants.put("gemini", "org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel");
        substanceVariants.put("graphite", "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
        substanceVariants.put("mariner", "org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel");
        substanceVariants.put("mist-aqua", "org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel");
        substanceVariants.put("mist-silver", "org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel");
        substanceVariants.put("moderate", "org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
        substanceVariants.put("nebula", "org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel");
        substanceVariants.put("nebula-brick-wall", "org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel");
        substanceVariants.put("officeblack", "org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel");
        substanceVariants.put("officeblue", "org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel");
        substanceVariants.put("officesilver", "org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel");
        substanceVariants.put("sahara", "org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel");

        // For JDK > 8
        radianceVariants.put("default", "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessLookAndFeel");

        radianceVariants.put("autumn", "org.pushingpixels.radiance.theming.api.skin.RadianceAutumnLookAndFeel");
        radianceVariants.put("business", "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessLookAndFeel");
        radianceVariants.put("business-black", "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessBlackSteelLookAndFeel");
        radianceVariants.put("business-blue", "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessBlueSteelLookAndFeel");
        radianceVariants.put("cerulean", "org.pushingpixels.radiance.theming.api.skin.RadianceCeruleanLookAndFeel"); /* NEW */
        radianceVariants.put("creme", "org.pushingpixels.radiance.theming.api.skin.RadianceCremeLookAndFeel");
        radianceVariants.put("creme-coffee", "org.pushingpixels.radiance.theming.api.skin.RadianceCremeCoffeeLookAndFeel");
        radianceVariants.put("dust", "org.pushingpixels.radiance.theming.api.skin.RadianceDustLookAndFeel");
        radianceVariants.put("dust-coffee", "org.pushingpixels.radiance.theming.api.skin.RadianceDustCoffeeLookAndFeel");
        radianceVariants.put("gemini", "org.pushingpixels.radiance.theming.api.skin.RadianceGeminiLookAndFeel");
        radianceVariants.put("graphite", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteLookAndFeel");
        radianceVariants.put("graphite-aqua", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteAquaLookAndFeel");/* NEW */
        radianceVariants.put("graphite-chalk", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteChalkLookAndFeel");/* NEW */
        radianceVariants.put("graphite-electric", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteElectricLookAndFeel");/* NEW */
        radianceVariants.put("graphite-glass", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteGlassLookAndFeel");/* NEW */
        radianceVariants.put("graphite-gold", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteGoldLookAndFeel");/* NEW */
        radianceVariants.put("graphite-sienna", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteSiennaLookAndFeel");/* NEW */
        radianceVariants.put("graphite-sunset", "org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteSunsetLookAndFeel");/* NEW */
        radianceVariants.put("green-magic", "org.pushingpixels.radiance.theming.api.skin.RadianceGreenMagicLookAndFeel");/* NEW */
        radianceVariants.put("magellan", "org.pushingpixels.radiance.theming.api.skin.RadianceMagellanLookAndFeel");/* NEW */
        radianceVariants.put("mariner", "org.pushingpixels.radiance.theming.api.skin.RadianceMarinerLookAndFeel");
        radianceVariants.put("mist-aqua", "org.pushingpixels.radiance.theming.api.skin.RadianceMistAquaLookAndFeel");
        radianceVariants.put("mist-silver", "org.pushingpixels.radiance.theming.api.skin.RadianceMistSilverLookAndFeel");
        radianceVariants.put("moderate", "org.pushingpixels.radiance.theming.api.skin.RadianceModerateLookAndFeel");
        radianceVariants.put("nebula", "org.pushingpixels.radiance.theming.api.skin.RadianceNebulaLookAndFeel");
        radianceVariants.put("nebula-amethyst", "org.pushingpixels.radiance.theming.api.skin.RadianceNebulaAmethystLookAndFeel"); /* NEW */
        radianceVariants.put("nebula-brick-wall", "org.pushingpixels.radiance.theming.api.skin.RadianceNebulaBrickWallLookAndFeel");
        radianceVariants.put("night-shade", "org.pushingpixels.radiance.theming.api.skin.RadianceNightShadeLookAndFeel"); /* NEW */
        radianceVariants.put("raven", "org.pushingpixels.radiance.theming.api.skin.RadianceRavenLookAndFeel"); /* NEW */
        radianceVariants.put("sahara", "org.pushingpixels.radiance.theming.api.skin.RadianceSaharaLookAndFeel");
        radianceVariants.put("sentinel", "org.pushingpixels.radiance.theming.api.skin.RadianceSentinelLookAndFeel");/* NEW */
        radianceVariants.put("twilight", "org.pushingpixels.radiance.theming.api.skin.RadianceTwilightLookAndFeel");/* NEW */

        //From radiance-theming-extras
        radianceVariants.put("field-of-wheat","org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceFieldOfWheatLookAndFeel");/* NEW */
        radianceVariants.put("harvest","org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceHarvestLookAndFeel");/* NEW */
        radianceVariants.put("magma","org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceMagmaLookAndFeel"); /* NEW */
        radianceVariants.put("officeblack","org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceOfficeBlack2007LookAndFeel");
        radianceVariants.put("officeblue","org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceOfficeBlue2007LookAndFeel");
        radianceVariants.put("officesilver","org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceOfficeSilver2007LookAndFeel");
        radianceVariants.put("streetlights","org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceStreetlightsLookAndFeel");/* NEW */

        looksVariants.put("windows", "com.jgoodies.looks.windows.WindowsLookAndFeel");
        looksVariants.put("plastic", "com.jgoodies.looks.plastic.PlasticLookAndFeel");
        looksVariants.put("plastic3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        looksVariants.put("plasticXP", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
    }

    private final Resources resources;
    private final Locales locales;
    private final DefaultVariables variables;
    private final Housekeeper housekeeper;
    private final PlatformModelMatcher matcher;

    @Inject
    public GUIInstallDataProvider(Resources resources,
                                  Locales locales,
                                  DefaultVariables variables,
                                  Housekeeper housekeeper,
                                  PlatformModelMatcher matcher) {
        this.resources = resources;
        this.locales = locales;
        this.variables = variables;
        this.housekeeper = housekeeper;
        this.matcher = matcher;
    }

    @Override
    public GUIInstallData loadInstallData()
    {
        try {
            final GUIInstallData guiInstallData = new GUIInstallData(variables, matcher.getCurrentPlatform());
            guiInstallData.setVariable(InstallData.INSTALLER_MODE, InstallData.INSTALLER_MODE_GUI);
            // Loads the installation data
            loadInstallData(guiInstallData, resources, matcher, housekeeper);
            loadGUIInstallData(guiInstallData, resources);
            loadInstallerRequirements(guiInstallData, resources);
            loadDynamicVariables(variables, guiInstallData, resources);
            loadDynamicConditions(guiInstallData, resources);
            loadDefaultLocale(guiInstallData, locales);
            // Load custom langpack if exist.
            AbstractInstallDataProvider.addCustomLangpack(guiInstallData, locales);
            // Load user input langpack if exist.
            AbstractInstallDataProvider.addUserInputLangpack(guiInstallData, locales);
            loadLookAndFeel(guiInstallData);
            if (UIManager.getColor("Button.background") != null) {
                guiInstallData.buttonsHColor = UIManager.getColor("Button.background");
            }
            // ENTER always presses button in focus
            UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
            return guiInstallData;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load GUI install data", e);
        }
    }

    /**
     * Loads the suitable L&F.
     *
     * @param installData the installation data
     * @throws Exception Description of the Exception
     */
    protected void loadLookAndFeel(final GUIInstallData installData) throws Exception
    {
        // Do we have any preference for this OS ?
        String syskey = "unix";
        if (OsVersion.IS_WINDOWS)
        {
            syskey = "windows";
        }
        else if (OsVersion.IS_OSX)
        {
            syskey = "mac";
        }

        LookAndFeel lookAndFeel = null;
        if (installData.guiPrefs.lookAndFeelMapping.containsKey(syskey))
        {
            lookAndFeel = installData.guiPrefs.lookAndFeelMapping.get(syskey);
        }

        // Let's use the system LAF
        // Resolve whether button icons should be used or not.
        boolean useButtonIcons = true;
        if (installData.guiPrefs.modifier.containsKey(MODIFIER_USE_BUTTON_ICONS)
                && "no".equalsIgnoreCase(installData.guiPrefs.modifier
                        .get(MODIFIER_USE_BUTTON_ICONS)))
        {
            useButtonIcons = false;
        }
        ButtonFactory.useButtonIcons(useButtonIcons);
        boolean useLabelIcons = true;
        if (installData.guiPrefs.modifier.containsKey(MODIFIER_USE_LABEL_ICONS)
                && "no".equalsIgnoreCase(installData.guiPrefs.modifier
                                                 .get(MODIFIER_USE_LABEL_ICONS)))
        {
            useLabelIcons = false;
        }
        LabelFactory.setUseLabelIcons(useLabelIcons);
        if (installData.guiPrefs.modifier.containsKey(MODIFIER_LABEL_FONT_SIZE))
        {  //'labelFontSize' modifier found in 'guiprefs'
            final String valStr =
                    installData.guiPrefs.modifier.get(MODIFIER_LABEL_FONT_SIZE);
            try
            {      //parse value and enter as label-font-size multiplier:
                LabelFactory.setLabelFontSize(Float.parseFloat(valStr));
            }
            catch (NumberFormatException ex)
            {      //error parsing value; log message
                logger.warning("Error parsing guiprefs '"+MODIFIER_LABEL_FONT_SIZE+"' value (" +
                                       valStr + ')');
            }
        }

        if (lookAndFeel == null)
        {
            if (!"mac".equals(syskey))
            {
                String syslaf = UIManager.getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(syslaf);
                if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel)
                {
                    ButtonFactory.useButtonIcons(useButtonIcons);
                }
            }
            return;
        }

        // Kunststoff (http://www.incors.org/)
        if (lookAndFeel.is(LookAndFeels.KUNSTSTOFF))
        {
            ButtonFactory.useHighlightButtons();
            // Reset the use button icons state because useHighlightButtons
            // make it always true.
            ButtonFactory.useButtonIcons(useButtonIcons);
            installData.buttonsHColor = new Color(255, 255, 255);
            @SuppressWarnings("unchecked")
			Class<javax.swing.LookAndFeel> lafClass = (Class<javax.swing.LookAndFeel>) Class.forName(
                    "com.incors.plaf.kunststoff.KunststoffLookAndFeel");
            @SuppressWarnings("unchecked")
			Class<MetalTheme> mtheme = (Class<MetalTheme>) Class.forName("javax.swing.plaf.metal.MetalTheme");
            Class[] params = {mtheme};
            @SuppressWarnings("unchecked")
			Class<IzPackKMetalTheme> theme = (Class<IzPackKMetalTheme>) Class.forName("com.izforge.izpack.gui.IzPackKMetalTheme");
            Method setCurrentThemeMethod = lafClass.getMethod("setCurrentTheme", params);

            // We invoke and place Kunststoff as our L&F
            javax.swing.LookAndFeel kunststoff = lafClass.newInstance();
            MetalTheme ktheme = theme.newInstance();
            Object[] kparams = {ktheme};
            UIManager.setLookAndFeel(kunststoff);
            setCurrentThemeMethod.invoke(kunststoff, kparams);
            return;
        }

        // Metouia (http://mlf.sourceforge.net/)
        if (lookAndFeel.is(LookAndFeels.METOUIA))
        {
            UIManager.setLookAndFeel("net.sourceforge.mlf.metouia.MetouiaLookAndFeel");
            return;
        }

        // Nimbus
        if (lookAndFeel.is(LookAndFeels.NIMBUS))
        {
            // Nimbus was included in JDK 6u10 but the packaging changed in JDK 7. Iterate to locate it
            // See http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html for more details
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    logger.info("Using laf " + info.getClassName());
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            return;
        }

        // JGoodies Looks (http://looks.dev.java.net/)
        if (lookAndFeel.is(LookAndFeels.LOOKS))
        {
            String variant = looksVariants.get("plasticXP");
            String variantName = lookAndFeel.getVariantName();
            if (looksVariants.containsKey(variantName))
            {
                variant = looksVariants.get(variantName);
            }
            logger.info("Using laf " + variant);
            UIManager.setLookAndFeel(variant);
            return;
        }

        // Substance (http://substance.dev.java.net/) for JDK <= 8
        // or Radians (https://github.com/kirill-grouchnikov/radiance) for JDK > 8
        if (lookAndFeel.is(LookAndFeels.SUBSTANCE))
        {
            final String variant;
            final String variantName = lookAndFeel.getVariantName();

            Map<String, String> variants = substanceVariants;
            if (JavaVersion.CURRENT.feature() > 8) {
                // Use Radiance
                variants = radianceVariants;
            }

            if (variants.containsKey(variantName))
            {
                variant = variants.get(variantName);
            }
            else
            {
                variant = variants.get("default");
            }

            logger.info("Using laf " + variant);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  try {
                      UIManager.setLookAndFeel(variant);
                      UIManager.getLookAndFeelDefaults().put("ClassLoader", JPanel.class.getClassLoader());
                      checkSubstanceLafLoaded();
                  } catch (Exception e) {
                      logger.log(Level.SEVERE, "Error loading Substance look and feel: " + e.getMessage(), e);
                    System.out.println("Substance Graphite failed to initialize");
                  }
                }
              });
        }
    }

    private void checkSubstanceLafLoaded() throws ClassNotFoundException
    {
        UIDefaults defaults = UIManager.getDefaults();
        String uiClassName = (String) defaults.get("PanelUI");
        ClassLoader cl = (ClassLoader) defaults.get("ClassLoader");
        ClassLoader classLoader = (cl != null) ? cl : JPanel.class.getClassLoader();
        Class aClass = (Class) defaults.get(uiClassName);

        logger.fine("PanelUI: " + uiClassName);
        logger.fine("ClassLoader: " + classLoader);
        logger.fine("Cached class: " + aClass);
        if (aClass != null)
        {
            return;
        }

        if (classLoader == null)
        {
            logger.fine("Using system loader to load " + uiClassName);
            aClass = Class.forName(uiClassName, true, Thread.currentThread().getContextClassLoader());
            logger.fine("Done loading");
        }
        else
        {
            logger.fine("Using custom loader to load " + uiClassName);
            aClass = classLoader.loadClass(uiClassName);
            logger.fine("Done loading");
        }
        if (aClass != null)
        {
            logger.fine("Loaded class: " + aClass.getName());
        }
        else
        {
            logger.fine("Couldn't load the class");
        }
    }

    /**
     * Load GUI preference information.
     *
     * @param installData the GUI installation data
     * @throws Exception
     */
    private void loadGUIInstallData(GUIInstallData installData, Resources resources) throws Exception
    {
        installData.guiPrefs = (GUIPrefs) resources.getObject("GUIPrefs");
    }

}
