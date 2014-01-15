package org.holoeverywhere.plugin.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

class HoloEverywhereExtension {
    public static final String HOLO_EVERYWHERE_GROUP = 'org.holoeverywhere'
    public static final String HOLO_EVERYWHERE_NAME = 'library'
    public static final String HOLO_EVERYWHERE_VERSION = '2.1.0'
    public static final String HOLO_EVERYWHERE_REPO = 'http://192.241.191.41/repo'
    public static final String HOLO_EVERYWHERE_SNAPSHOT_REPO = 'http://192.241.191.41/snapshot'

    public static final String SUPPORT_V4_GROUP = 'com.android.support'
    public static final String SUPPORT_V4_NAME = 'support-v4'
    public static final String SUPPORT_V4_VERSION = '18.0.4'

    public static class Addon {
        def String group, name, version

        public Addon(String name) {
            this(HOLO_EVERYWHERE_GROUP, name);
        }

        public Addon(String group, String name) {
            this(group, name, null)
        }

        public Addon(String group, String name, String version) {
            this.group = group
            this.name = name
            this.version = version;
        }

        public int hashCode() {
            return toString().hashCode()
        }

        public String toString() {
            return group + ':' + name + ':' + version
        }
    }

    HoloEverywhereExtension(Project project, Instantiator instantiator) {
        this.project = project
        this.instantiator = instantiator
        this.app = new AppContainer(project)
        this.library = new LibraryContainer(this)
        this.supportV4 = new SupportV4Container(this)
        this.repository = new RepositoryContainer(this)
        this.resbuilder = new ResbuilderContainer(project, instantiator)
        this.upload = new UploadContainer(project)
        this.signing = new SigningContainer(project)

        this.addons = project.container(Addon, new NamedDomainObjectFactory<Addon>() {
            @Override
            def Addon create(String name) {
                String[] parts = name.split(':')
                return parts.length == 1 ? new Addon("addon-${name}") :
                        new Addon(parts[0], parts[1], parts.length == 3 ? parts[2] : null)
            }
        })
        this.addons.metaClass.propertyMissing = { String name ->
            this.addons.maybeCreate(name)
        }
    }

    private final Project project
    private final Instantiator instantiator
    def final NamedDomainObjectContainer<Addon> addons
    def final AppContainer app
    def final LibraryContainer library
    def final SupportV4Container supportV4
    def final RepositoryContainer repository
    def final ResbuilderContainer resbuilder
    def final UploadContainer upload
    def final SigningContainer signing
    def boolean applyLibraryPlugin = true
    def boolean applyAppPlugin = true
    def IncludeContainer.Include include = IncludeContainer.Include.Yes
    def String configuration = 'compile'

    def NamedDomainObjectContainer<Addon> addons(Closure<?> closure) {
        return addons.configure(closure)
    }

    def AppContainer app(Closure<?> closure) {
        return app.configure(closure);
    }

    def LibraryContainer library(Closure<?> closure) {
        return library.configure(closure);
    }

    def SupportV4Container supportV4(Closure<?> closure) {
        return supportV4.configure(closure);
    }

    def RepositoryContainer repository(Closure<?> closure) {
        return repository.configure(closure);
    }

    def ResbuilderContainer resbuilder(Closure<?> closure) {
        return resbuilder.configure(closure)
    }

    def UploadContainer upload(Closure<?> closure) {
        return upload.configure(closure);
    }

    def SigningContainer signing(Closure<?> closure) {
        return signing.configure(closure)
    }

    def void include(String name) {
        this.include = IncludeContainer.Include.find(name, IncludeContainer.Include.Inhert)
    }

    private static final String EXTENSION_NAME = 'holoeverywhere'

    public static HoloEverywhereExtension getOrCreateExtension(Project project, Instantiator instantiator) {
        project.extensions.findByName(EXTENSION_NAME) as HoloEverywhereExtension ?: project.extensions.create(EXTENSION_NAME, HoloEverywhereExtension, project, instantiator)
    }
}
