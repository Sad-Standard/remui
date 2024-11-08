# With IntelliJ (recommended)

#### Clone the project

<procedure title="Clone the project with Git CLI" id="clone_the_project_with_git_cli" collapsible="true">
    <step>
        <p>Open a terminal session and run:</p>
        <code-block lang="bash">
            git clone git@gitlab.dasorl.com:common-libraries/remui.git
        </code-block>
    </step>
</procedure>
<procedure title="Clone the project with the GitLab Plugin" id="clone_the_project_with_the_git_lab_plugin" collapsible="true">
    <note>Following this method provides integration with GitLab for interacting with Merge Requests within IntelliJ</note>
    <step>
        <p>Go to the IntelliJ <a href="https://plugins.jetbrains.com/plugin/22857-gitlab">GitLab Plugin</a> page</p>
    </step>
    <step>
        <p>Wait for the <code>Install to ...</code> button to appear and click it to install the plugin for you</p>
    </step>
    <step>
        <p>Once GitLab is fully installed, navigate to the GitLab settings tab <ui-path>File | Settings | Version Control | GitLab</ui-path></p>
    </step>
    <step>
        <p>Click the <control>+</control> button or press <shortcut>Alt+Insert</shortcut> to add a new GitLab server</p>
    </step>
    <step>
        <p> 
            Create a new access token by going 
            <a href="https://gitlab.dasorl.com/-/user_settings/personal_access_tokens">here</a> and adding the permissions 
            <code>api</code> and <code>read_user</code>
        </p>
    </step>
    <step>
        <p>Set the server field to <code>https://gitlab.dasorl.com</code>and the token field to the token you just created</p> 
        <warning>Do not save this token anywhere else</warning>
    </step>
    <step>
        <p>Navigate to <ui-path>File | New | Project from Version Control</ui-path> then <ui-path>GitLab</ui-path> and find and 
        select the project <code>Common Libraries / remui</code></p>
    </step>
</procedure>



#### Run the playground

<procedure>
    <step>
        <p>Open IntelliJ's <ui-path>Run Anything</ui-path> window by double pressing <shortcut>Ctrl</shortcut></p>
    </step>
    <step>
        <p>In the text box, search for <code>Playground A</code> or <code>Playground B</code> and press <shortcut>Enter</shortcut></p>
    </step>
    <step>
        <p>Open <a href="http://localhost:8080"/> in your browser</p>
        <note>On first run things might take a little so you might need to refresh a few times</note>
    </step>
</procedure>


#### Modify the playground

<procedure>
    <step>Open IntelliJ's <ui-path>Search Everywhere</ui-path> window by double tapping <shortcut>Shift</shortcut></step>
    <step>Search for <code>ServerPlaygroundA.kt</code> or <code>ServerPlaygroundB.kt</code> then press <shortcut>Enter</shortcut></step>
    <step>Make your changes</step>
    <step>Rerun the corresponding Run Configuration</step>
</procedure>

#### Enable K2 mode
<note>This step is optional, for now</note>
<procedure>
    <step>Navigate to <ui-path>File | Settings | Languages & Frameworks | Kotlin</ui-path></step>
    <step>
        Ensure <code>Enable K2 mode</code> is enabled.
        <note>You may have to restart IntelliJ</note>
    </step>
</procedure>


#### Enable non bundled compiler plugins (extra-optional)

<note>
This step is EXTRA optional. You must <a href="#enable-k2-mode">enable K2 mode</a> first
</note>
<note>
This setting is only need to make sure the IDE is aware of the additional analysis Remui's compiler plugin provides. The
project's compiler plugin will be in use soon... (This setting also won't be needed once K2 support has been stabilized)
</note>

<procedure>
    <step>Open IntelliJ's <ui-path>Search Everywhere</ui-path> window by double tapping <shortcut>Shift</shortcut></step>
    <step>Type <code>registry...</code> into the textbox and press <shortcut>Enter</shortcut></step>
    <step>Start typing <code>kotlin.k2.only</code> to search and ensure that the setting <code>kotlin.k2.only.bundled.compiler.plugins.enabled</code> is <strong>not</strong> checked</step>
    <step>Press <code>Close</code> and restart IntelliJ</step>
</procedure>
