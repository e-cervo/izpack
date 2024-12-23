/*
 * IzPack - Copyright 2001-2008 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2003 Tino Schwarze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.panels.compile;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.google.inject.Inject;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.substitutor.VariableSubstitutor;
import com.izforge.izpack.gui.ButtonFactory;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.installer.gui.IzPanel;
import com.izforge.izpack.installer.unpacker.IUnpacker;
import com.izforge.izpack.util.PlatformModelMatcher;

/**
 * The compile panel class.
 * <p/>
 * This class allows .java files to be compiled after installation.
 * <p/>
 * Parts of the code have been taken from InstallPanel.java and modified a lot.
 *
 * @author Tino Schwarze
 * @author Julien Ponge
 */
public class CompilePanel extends IzPanel implements ActionListener, CompileHandler
{
    private static final long serialVersionUID = 3258408430669674552L;

    /**
     * The combobox for compiler selection.
     */
    private JComboBox compilerComboBox;

    /**
     * The combobox for compiler argument selection.
     */
    private JComboBox argumentsComboBox;

    /**
     * The start button.
     */
    private JButton startButton;

    /**
     * The browse button.
     */
    private JButton browseButton;

    /**
     * The operation label .
     */
    private JLabel opLabel;

    /**
     * The pack progress bar.
     */
    private JProgressBar packProgressBar;

    /**
     * The overall progress bar.
     */
    private JProgressBar overallProgressBar;

    /**
     * True if the compilation has been done.
     */
    private boolean validated = false;

    /**
     * The compilation worker. Does all the work.
     */
    private CompileWorker worker;

    /**
     * Number of jobs to compile. Used for progress indication.
     */
    private int noOfJobs;

    /**
     * Constructs a <tt>CompilePanel</tt>.
     *
     * @param panel               the panel meta-data
     * @param parent              the parent window
     * @param variableSubstitutor the variable substituter
     * @param installData         the installation data
     * @param resources           the resources
     * @param unpacker            the unpacker
     * @throws IOException for any I/O error
     */
    @Inject
    public CompilePanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources,
                        VariableSubstitutor variableSubstitutor, IUnpacker unpacker,
                        PlatformModelMatcher matcher) throws IOException
    {
        super(panel, parent, installData, resources);
        unpacker.setProgressListener(this);
        this.worker = new CompileWorker(installData, this, variableSubstitutor, resources, matcher);

        GridBagConstraints gridBagConstraints;

        JLabel heading = new JLabel();
        // put everything but the heading into it's own panel
        // (to center it vertically)
        JPanel subpanel = new JPanel();
        JLabel compilerLabel = new JLabel();
        compilerComboBox = new JComboBox();
        this.browseButton = ButtonFactory.createButton(getString("CompilePanel.browse"), installData.buttonsHColor);
        JLabel argumentsLabel = new JLabel();
        this.argumentsComboBox = new JComboBox();
        this.startButton = ButtonFactory.createButton(getString("CompilePanel.start"), installData.buttonsHColor);

        JLabel tipLabel = LabelFactory.create(getString("CompilePanel.tip"), parent.getIcons().get("tip"),
                SwingConstants.TRAILING);
        this.opLabel = new JLabel();
        packProgressBar = new JProgressBar();

        JLabel overallLabel = new JLabel();
        this.overallProgressBar = new JProgressBar();

        setLayout(new GridBagLayout());

        Font font = heading.getFont();
        font = font.deriveFont(Font.BOLD, font.getSize() * 2.0f);
        heading.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setText(getString("CompilePanel.heading"));
        heading.setVerticalAlignment(SwingConstants.TOP);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(heading, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        add(subpanel, gridBagConstraints);

        subpanel.setLayout(new GridBagLayout());

        int row = 0;

        compilerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        compilerLabel.setLabelFor(compilerComboBox);
        compilerLabel.setText(getString("CompilePanel.choose_compiler"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // gridBagConstraints.weighty = 0.1;
        subpanel.add(compilerLabel, gridBagConstraints);

        compilerComboBox.setEditable(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // gridBagConstraints.weighty = 0.1;

        for (String availableCompiler : this.worker.getAvailableCompilers())
        {
            compilerComboBox.addItem(availableCompiler);
        }

        subpanel.add(compilerComboBox, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        browseButton.addActionListener(this);
        subpanel.add(browseButton, gridBagConstraints);

        argumentsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        argumentsLabel.setLabelFor(argumentsComboBox);
        argumentsLabel.setText(getString("CompilePanel.additional_arguments"));
        // argumentsLabel.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        // gridBagConstraints.weighty = 0.1;
        subpanel.add(argumentsLabel, gridBagConstraints);

        argumentsComboBox.setEditable(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        // gridBagConstraints.weighty = 0.1;

        for (String availableArgument : this.worker.getAvailableArguments())
        {
            argumentsComboBox.addItem(availableArgument);
        }

        subpanel.add(argumentsComboBox, gridBagConstraints);

        // leave some space above the label
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        subpanel.add(tipLabel, gridBagConstraints);

        opLabel.setText(" ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        subpanel.add(opLabel, gridBagConstraints);

        packProgressBar.setValue(0);
        packProgressBar.setString(getString("CompilePanel.progress.initial"));
        packProgressBar.setStringPainted(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        subpanel.add(packProgressBar, gridBagConstraints);

        overallLabel.setText(getString("CompilePanel.progress.overall"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        subpanel.add(overallLabel, gridBagConstraints);

        overallProgressBar.setValue(0);
        overallProgressBar.setString("");
        overallProgressBar.setStringPainted(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = row++;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        subpanel.add(overallProgressBar, gridBagConstraints);

        startButton.setText(getString("CompilePanel.start"));
        startButton.addActionListener(this);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridy = row;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        // leave some space above the button
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        subpanel.add(startButton, gridBagConstraints);
    }

    @Override
    public boolean isValidated()
    {
        return validated;
    }

    /**
     * Action function, called when the start button is pressed.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == this.startButton)
        {
            this.worker.setCompiler((String) this.compilerComboBox.getSelectedItem());

            this.worker.setCompilerArguments((String) this.argumentsComboBox.getSelectedItem());

            this.blockGUI();
            this.worker.startThread();
        }
        else if (e.getSource() == this.browseButton)
        {
            this.parent.blockGUI();
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File((String) this.compilerComboBox.getSelectedItem())
                                                .getParentFile());
            int result = chooser.showDialog(this.parent, getString("CompilePanel.browse.approve"));
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File file_chosen = chooser.getSelectedFile();

                if (file_chosen.isFile())
                {
                    this.compilerComboBox.setSelectedItem(file_chosen.getAbsolutePath());
                }

            }

            this.parent.releaseGUI();
        }

    }

    /**
     * Block the GUI - disalow input.
     */
    private void blockGUI()
    {
        // disable all controls
        this.startButton.setEnabled(false);
        this.browseButton.setEnabled(false);
        this.compilerComboBox.setEnabled(false);
        this.argumentsComboBox.setEnabled(false);

        this.parent.blockGUI();
    }

    /**
     * Release the GUI - allow input.
     *
     * @param allowconfig allow the user to enter new configuration
     */
    private void releaseGUI(boolean allowconfig)
    {
        // disable all controls
        if (allowconfig)
        {
            this.startButton.setEnabled(true);
            this.browseButton.setEnabled(true);
            this.compilerComboBox.setEnabled(true);
            this.argumentsComboBox.setEnabled(true);
        }

        this.parent.releaseGUI();
    }

    @Override
    public void handleCompileError(CompileResult error)
    {
        String message = error.getMessage();
        opLabel.setText(message);
        CompilerErrorDialog dialog = new CompilerErrorDialog(parent, message, installData.buttonsHColor);
        dialog.show(error);

        if (dialog.getResult() == CompilerErrorDialog.RESULT_IGNORE)
        {
            error.setAction(CompileResult.ACTION_CONTINUE);
        }
        else if (dialog.getResult() == CompilerErrorDialog.RESULT_RECONFIGURE)
        {
            error.setAction(CompileResult.ACTION_RECONFIGURE);
        }
        else
        // default case: abort
        {
            error.setAction(CompileResult.ACTION_ABORT);
        }

    }

    @Override
    public void startAction(String name, int noOfJobs1)
    {
        this.noOfJobs = noOfJobs1;
        overallProgressBar.setMaximum(noOfJobs1);
        parent.lockPrevButton();
    }

    @Override
    public void stopAction()
    {
        CompileResult result = this.worker.getResult();

        this.releaseGUI(result.isReconfigure());

        if (result.isContinue())
        {
            parent.lockPrevButton();

            packProgressBar.setString(getString("CompilePanel.progress.finished"));
            packProgressBar.setEnabled(false);
            packProgressBar.setValue(packProgressBar.getMaximum());

            overallProgressBar.setValue(this.noOfJobs);
            String no_of_jobs = Integer.toString(this.noOfJobs);
            overallProgressBar.setString(no_of_jobs + " / " + no_of_jobs);
            overallProgressBar.setEnabled(false);

            opLabel.setText(" ");
            opLabel.setEnabled(false);

            validated = true;
            installData.setInstallSuccess(true);
            if (installData.getPanels().indexOf(this) != (installData.getPanels().size() - 1))
            {
                parent.unlockNextButton();
            }
        }
        else
        {
            installData.setInstallSuccess(false);
        }

    }

    @Override
    public void progress(int val, String msg)
    {
        // Debug.trace ("progress: " + val + " " + msg);
        packProgressBar.setValue(val + 1);
        opLabel.setText(msg);
    }

    @Override
    public void nextStep(String jobName, int max, int jobNo)
    {
        packProgressBar.setValue(0);
        packProgressBar.setMaximum(max);
        packProgressBar.setString(jobName);

        opLabel.setText("");

        overallProgressBar.setValue(jobNo);
        overallProgressBar.setString(Integer.toString(jobNo) + " / "
                                             + Integer.toString(this.noOfJobs));
    }

    @Override
    public void setSubStepNo(int max)
    {
        packProgressBar.setMaximum(max);
    }

    @Override
    public void progress(String message)
    {
        // no-op
    }

    @Override
    public void restartAction(String name, String overallMessage, String tip, int steps)
    {
        startAction(name, steps);
    }

    @Override
    public void panelActivate()
    {
        compilerComboBox.removeAllItems();

        // get compilers again (because they might contain variables from former
        // panels)
        for (String availableCompiler : this.worker.getAvailableCompilers())
        {
            compilerComboBox.addItem(availableCompiler);
        }

        // We clip the panel
        Dimension dim = parent.getPanelsContainerSize();
        dim.width -= (dim.width / 4);
        dim.height = 150;
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);

        parent.lockNextButton();
    }

    /**
     * Show a special dialog for compiler errors.
     * <p/>
     * This dialog is neccessary because we have lots of information if compilation failed. We'd
     * also like the user to chose whether to ignore the error or not.
     */
    protected class CompilerErrorDialog extends JDialog implements ActionListener
    {

        private static final long serialVersionUID = 3762537797721995317L;

        /**
         * user closed the dialog without pressing "Ignore" or "Abort"
         */
        public static final int RESULT_NONE = 0;

        /**
         * user pressed "Ignore" button
         */
        public static final int RESULT_IGNORE = 23;

        /**
         * user pressed "Abort" button
         */
        public static final int RESULT_ABORT = 42;

        /**
         * user pressed "Reconfigure" button
         */
        public static final int RESULT_RECONFIGURE = 47;

        /**
         * visual goodie: button hightlight color
         */
        private java.awt.Color buttonHColor = null;

        /**
         * Creates new form compilerErrorDialog
         *
         * @param parent       parent to be used
         * @param title        String to be used as title
         * @param buttonHColor highlight color to be used
         */
        public CompilerErrorDialog(java.awt.Frame parent, String title, java.awt.Color buttonHColor)
        {
            super(parent, title, true);
            this.buttonHColor = buttonHColor;
            initComponents();
        }

        /**
         * This method is called from within the constructor to initialize the form.
         * <p/>
         * Generated with help from NetBeans IDE.
         */
        private void initComponents()
        {
            JPanel errorMessagePane = new JPanel();
            errorMessageText = new JTextArea();
            JTextArea seeBelowText = new JTextArea();
            JTabbedPane errorDisplayPane = new JTabbedPane();
            JScrollPane commandScrollPane = new JScrollPane();
            commandText = new JTextArea();
            JScrollPane stdOutScrollPane = new JScrollPane();
            stdOutText = new JTextArea();
            JScrollPane stdErrScrollPane = new JScrollPane();
            stdErrText = new JTextArea();
            JPanel buttonsPanel = new JPanel();
            reconfigButton = ButtonFactory.createButton(getString("CompilePanel.error.reconfigure"), this.buttonHColor);
            ignoreButton = ButtonFactory.createButton(getString("CompilePanel.error.ignore"), this.buttonHColor);
            abortButton = ButtonFactory.createButton(getString("CompilePanel.error.abort"), this.buttonHColor);

            addWindowListener(new java.awt.event.WindowAdapter()
            {

                public void windowClosing(java.awt.event.WindowEvent evt)
                {
                    closeDialog();
                }
            });

            errorMessagePane.setLayout(new BoxLayout(errorMessagePane, BoxLayout.Y_AXIS));
            errorMessageText.setBackground(super.getBackground());
            errorMessageText.setEditable(false);
            errorMessageText.setLineWrap(true);
            // errorMessageText.setText("The compiler does not seem to work. See
            // below for the command we tried to execute and the results.");
            // errorMessageText.setToolTipText("null");
            errorMessageText.setWrapStyleWord(true);
            errorMessagePane.add(errorMessageText);

            seeBelowText.setBackground(super.getBackground());
            seeBelowText.setEditable(false);
            seeBelowText.setLineWrap(true);
            seeBelowText.setWrapStyleWord(true);
            seeBelowText.setText(getString("CompilePanel.error.seebelow"));
            errorMessagePane.add(seeBelowText);

            getContentPane().add(errorMessagePane, java.awt.BorderLayout.NORTH);

            // use 12pt monospace font for compiler output etc.
            Font output_font = new Font("Monospaced", Font.PLAIN, 12);

            // errorDisplayPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            // errorDisplayPane.setName("null");
            commandText.setFont(output_font);
            commandText.setEditable(false);
            commandText.setRows(10);
            commandText.setColumns(82);
            commandText.setWrapStyleWord(true);
            commandText.setLineWrap(true);
            // commandText.setText("akjfkajfeafjakefjakfkaejfja");
            commandScrollPane.setViewportView(commandText);

            errorDisplayPane.addTab("Command", commandScrollPane);

            stdOutText.setFont(output_font);
            stdOutText.setEditable(false);
            stdOutText.setWrapStyleWord(true);
            stdOutText.setLineWrap(true);
            stdOutScrollPane.setViewportView(stdOutText);

            errorDisplayPane.addTab("Standard Output", null, stdOutScrollPane);

            stdErrText.setFont(output_font);
            stdErrText.setEditable(false);
            stdErrText.setWrapStyleWord(true);
            stdErrText.setLineWrap(true);
            stdErrScrollPane.setViewportView(stdErrText);

            errorDisplayPane.addTab("Standard Error", null, stdErrScrollPane);

            getContentPane().add(errorDisplayPane, java.awt.BorderLayout.CENTER);

            buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

            reconfigButton.addActionListener(this);
            buttonsPanel.add(reconfigButton);

            ignoreButton.addActionListener(this);
            buttonsPanel.add(ignoreButton);

            abortButton.addActionListener(this);
            buttonsPanel.add(abortButton);

            getContentPane().add(buttonsPanel, java.awt.BorderLayout.SOUTH);

            pack();
        }

        /**
         * Close the panel.
         */
        void closeDialog()
        {
            setVisible(false);
            dispose();
        }

        /**
         * Shows the given errors
         *
         * @param error error messages to be shown
         */
        public void show(CompileResult error)
        {
            this.errorMessageText.setText(error.getMessage());
            this.commandText.setText(error.getCmdline());
            this.stdOutText.setText(error.getStdout());
            this.stdErrText.setText(error.getStderr());
            super.setVisible(true);
        }

        /**
         * Returns the result of this dialog.
         *
         * @return the result of this dialog
         */
        public int getResult()
        {
            return this.result;
        }

        public void actionPerformed(ActionEvent e)
        {
            boolean closenow = false;

            if (e.getSource() == this.ignoreButton)
            {
                this.result = RESULT_IGNORE;
                closenow = true;
            }
            else if (e.getSource() == this.abortButton)
            {
                this.result = RESULT_ABORT;
                closenow = true;
            }
            else if (e.getSource() == this.reconfigButton)
            {
                this.result = RESULT_RECONFIGURE;
                closenow = true;
            }

            if (closenow)
            {
                this.setVisible(false);
                this.dispose();
            }

        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private JTextArea commandText;

        // private JScrollPane stdOutScrollPane;
        private JTextArea stdErrText;

        // private JPanel buttonsPanel;
        // private JScrollPane commandScrollPane;
        private JTextArea errorMessageText;

        // private JScrollPane stdErrScrollPane;
        private JButton ignoreButton;

        private JTextArea stdOutText;

        private JButton abortButton;

        private JButton reconfigButton;

        // private JTabbedPane errorDisplayPane;
        // End of variables declaration//GEN-END:variables

        private int result = RESULT_NONE;
    }
}
