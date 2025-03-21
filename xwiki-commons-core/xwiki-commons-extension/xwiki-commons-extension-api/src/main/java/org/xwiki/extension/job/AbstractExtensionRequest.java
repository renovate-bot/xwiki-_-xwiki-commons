/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.extension.job;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.xwiki.extension.Extension;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.ExtensionRewriter;
import org.xwiki.extension.repository.CoreExtensionRepository;
import org.xwiki.job.AbstractRequest;
import org.xwiki.job.Request;

/**
 * Base class for extension manipulation related {@link Request} implementations.
 *
 * @version $Id$
 * @since 4.0M1
 */
public abstract class AbstractExtensionRequest extends AbstractRequest implements ExtensionRequest
{
    /**
     * @see #getExtensions()
     */
    public static final String PROPERTY_EXTENSIONS = "extensions";

    /**
     * @see #getExcludedExtensions()
     */
    public static final String PROPERTY_EXCLUDEDEXTENSIONS = "extensions.excluded";

    /**
     * @see #getNamespaces()
     */
    public static final String PROPERTY_NAMESPACES = "namespaces";

    /**
     * @see #isRootModificationsAllowed()
     */
    public static final String PROPERTY_ROOTMODIFICATIONSALLOWED = "rootModificationsAllowed";

    /**
     * @see #isUninstallAllowed()
     */
    public static final String PROPERTY_UNINSTALLALLOWED = "uninstallAllowed";

    /**
     * @see #isInstalledIgnored()
     * @since 15.0RC1
     */
    public static final String PROPERTY_INSTALLEDIGNORED = "installedIgnored";

    /**
     * @see #getRewriter()
     */
    public static final String PROPERTY_REWRITER = "rewriter";

    /**
     * @see #getCoreExtensionRepository()
     * @since 15.0RC1
     */
    public static final String PROPERTY_COREEXTENSIONREPOSITORY = "coreExtensionRepository";

    /**
     * Serialization identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Expose an {@link Extension} as an {@link ExtensionId}.
     * 
     * @version $Id$
     * @since 15.2RC1
     */
    public class ExtensionExtensionId extends ExtensionId
    {
        /**
         * Serialization identifier.
         */
        private static final long serialVersionUID = 1L;

        private final Extension extension;

        /**
         * @param extension the extension
         */
        public ExtensionExtensionId(Extension extension)
        {
            super(extension.getId().getId(), extension.getId().getVersion());

            this.extension = extension;
        }

        /**
         * @return the extension
         */
        public Extension getExtension()
        {
            return this.extension;
        }
    }

    /**
     * Default constructor.
     */
    public AbstractExtensionRequest()
    {
        setProperty(PROPERTY_EXTENSIONS, new ArrayList<ExtensionId>());
        setProperty(PROPERTY_EXCLUDEDEXTENSIONS, new HashSet<ExtensionId>());
    }

    /**
     * @param request the request to copy
     */
    public AbstractExtensionRequest(Request request)
    {
        super(request);

        Collection<ExtensionId> extensions = getExtensions();
        if (extensions == null) {
            setProperty(PROPERTY_EXTENSIONS, new ArrayList<ExtensionId>());
        }
    }

    @Override
    public Collection<ExtensionId> getExtensions()
    {
        return getProperty(PROPERTY_EXTENSIONS);
    }

    /**
     * @return extensions to not take into account
     */
    public Collection<ExtensionId> getExcludedExtensions()
    {
        return getProperty(PROPERTY_EXCLUDEDEXTENSIONS);
    }

    /**
     * @return the extension to identify as core extensions
     * @since 15.0RC1
     */
    public Collection<ExtensionId> getCoreExtensions()
    {
        return getProperty(PROPERTY_EXCLUDEDEXTENSIONS);
    }

    @Override
    public Collection<String> getNamespaces()
    {
        return getProperty(PROPERTY_NAMESPACES);
    }

    @Override
    public boolean hasNamespaces()
    {
        Collection<String> namespaces = getNamespaces();

        return namespaces != null && !namespaces.isEmpty();
    }

    /**
     * @param extensionId the extension identifier
     */
    public void addExtension(ExtensionId extensionId)
    {
        getExtensions().add(extensionId);
    }

    /**
     * @param extension the extension to validate and for which to resolve dependencies
     * @since 15.2RC1
     */
    public void addExtension(Extension extension)
    {
        getExtensions().add(new ExtensionExtensionId(extension));
    }

    /**
     * @param extensionId the extension identifier
     */
    public void addExcludedExtension(ExtensionId extensionId)
    {
        getExcludedExtensions().add(extensionId);
    }

    /**
     * @param extensionId the extension identifier
     * @since 15.0RC1
     */
    public void addCoreExtension(ExtensionId extensionId)
    {
        getCoreExtensions().add(extensionId);
    }

    /**
     * @param namespace the namespace
     */
    public void addNamespace(String namespace)
    {
        Collection<String> namespaces = getNamespaces();

        if (namespaces == null) {
            namespaces = new ArrayList<>();
            setProperty(PROPERTY_NAMESPACES, namespaces);
        }

        namespaces.add(namespace);
    }

    @Override
    public boolean isRootModificationsAllowed()
    {
        return getProperty(PROPERTY_ROOTMODIFICATIONSALLOWED, true);
    }

    /**
     * @param allowed indicate if it's allowed to do modifications on root namespace during the job execution (not taken
     *            into account if the target of the request is root namespace)
     */
    public void setRootModificationsAllowed(boolean allowed)
    {
        setProperty(PROPERTY_ROOTMODIFICATIONSALLOWED, allowed);
    }

    /**
     * Allow modifying manipulated {@link Extension}s on the fly (change allowed namespaces, dependencies, etc.).
     *
     * @param rewriter the filter
     * @since 8.4.2
     * @since 9.0RC1
     */
    @Transient
    public void setRewriter(ExtensionRewriter rewriter)
    {
        setProperty(PROPERTY_REWRITER, rewriter);
    }

    @Override
    @Transient
    public ExtensionRewriter getRewriter()
    {
        return getProperty(PROPERTY_REWRITER);
    }

    /**
     * @param repository the repository to use to find core extensions
     * @since 15.0RC1
     */
    @Transient
    public void setCoreExtensionRepository(CoreExtensionRepository repository)
    {
        setProperty(PROPERTY_COREEXTENSIONREPOSITORY, repository);
    }

    @Override
    @Transient
    public CoreExtensionRepository getCoreExtensionRepository()
    {
        return getProperty(PROPERTY_COREEXTENSIONREPOSITORY);
    }

    @Override
    public boolean isUninstallAllowed()
    {
        return getProperty(PROPERTY_UNINSTALLALLOWED, true);
    }

    /**
     * @param allowed true if it's allowed remove extension in conflict with the new extension(s)
     * @since 9.1RC1
     */
    public void setUninstallAllowed(boolean allowed)
    {
        setProperty(PROPERTY_UNINSTALLALLOWED, allowed);
    }

    @Override
    public boolean isInstalledIgnored()
    {
        return getProperty(PROPERTY_INSTALLEDIGNORED, false);
    }

    /**
     * @param ignored true if already installed extensions should not be taken into account while resolving the install
     *            plan
     * @since 15.0CR1
     */
    public void setInstalledIgnored(boolean ignored)
    {
        setProperty(PROPERTY_INSTALLEDIGNORED, ignored);
    }
}
