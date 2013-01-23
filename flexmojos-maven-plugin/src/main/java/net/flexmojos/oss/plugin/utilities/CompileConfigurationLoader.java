/**
 * Flexmojos is a set of maven goals to allow maven users to compile, optimize and test Flex SWF, Flex SWC, Air SWF and Air SWC.
 * Copyright (C) 2008-2012  Marvin Froeder <marvin@flexmojos.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.flexmojos.oss.plugin.utilities;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO delete when this issue is done http://jira.codehaus.org/browse/MECLIPSE-417
 *
 * @author velo
 */
public class CompileConfigurationLoader {

    public static String getCompilerPluginSetting(MavenProject project, String optionName) {
        Xpp3Dom value = getCompilerPluginConfiguration(project, optionName);
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    @SuppressWarnings("unchecked")
    public static Xpp3Dom getCompilerPluginConfiguration(MavenProject project, String optionName) {
        Xpp3Dom value = findCompilerPluginSettingInPlugins(project.getModel().getBuild().getPlugins(), optionName);
        if (value == null && project.getModel().getBuild().getPluginManagement() != null) {
            value =
                    findCompilerPluginSettingInPlugins(project.getModel().getBuild().getPluginManagement().getPlugins(),
                            optionName);
        }
        return value;
    }

    /**
     * Returns a compiler plugin settings from a list of plugins .
     *
     * @param project maven project
     * @return option value (may be null)
     */
    @SuppressWarnings("unchecked")
    private static Xpp3Dom findCompilerPluginSettingInPlugins(List<Plugin> plugins, String optionName) {

        for (Iterator<Plugin> it = plugins.iterator(); it.hasNext(); ) {
            Plugin plugin = (Plugin) it.next();

            if (plugin.getArtifactId().equals("flexmojos-maven-plugin")) {
                Xpp3Dom o = (Xpp3Dom) plugin.getConfiguration();

                Xpp3Dom value = null;
                // this is the default setting
                if (o != null && o.getChild(optionName) != null) {
                    value = o.getChild(optionName);
                }

                List<PluginExecution> executions = plugin.getExecutions();

                // a different source/target version can be configured for test
                // sources compilation
                for (Iterator<PluginExecution> iter = executions.iterator(); iter.hasNext(); ) {
                    PluginExecution execution = (PluginExecution) iter.next();
                    o = (Xpp3Dom) execution.getConfiguration();

                    if (o != null && o.getChild(optionName) != null) {
                        value = o.getChild(optionName);
                    }
                }

                return value;
            }
        }
        return null;
    }

    public static String[] getCompilerPluginSettings(MavenProject project, String optionName) {
        Xpp3Dom value = getCompilerPluginConfiguration(project, optionName);
        if (value == null) {
            return null;
        }

        Xpp3Dom[] children = value.getChildren();
        String[] values = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            Xpp3Dom child = children[i];
            values[i] = child.getValue();
        }

        return values;
    }

    public static Map<String, String> getCompilerPluginSettingsMap(MavenProject project, String optionName) {
        final Xpp3Dom value = getCompilerPluginConfiguration(project, optionName);
        if (value == null) {
            return null;
        }

        final Xpp3Dom[] keys = value.getChildren();
        final Map<String, String> values = new HashMap<String, String>(keys.length);
        for (Xpp3Dom key : keys) {
            values.put(key.getName(), key.getValue());
        }

        return values;
    }
}
