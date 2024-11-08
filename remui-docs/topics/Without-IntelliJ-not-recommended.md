# Without IntelliJ (not recommended)

<warning>
Kotlin is a language designed to be used with an intelligent IDE that can rapidly provide code feedback. It is more 
likely that the lack of analysis will lead to errors and frustration.
</warning>


### Clone the project
<procedure title="Clone the project">
    <step>
        <p>Run the following to clone the project</p>
        <code-block lang="bash">
            git clone git@gitlab.dasorl.com:common-libraries/remui.git
        </code-block>
    </step>
    <step>
        <p>Enter the project root directory</p>
        <code-block lang="bash">
            cd remui
        </code-block>
    </step>
</procedure>

### Start the client web server
<procedure>
<step>
    <p>Run the following to start the client web server</p>
    <code-block lang="bash">
        ./gradlew :playground-js:jsBrowserDevelopmentRun --continuous
    </code-block>
</step>
<note>
    <p>
        The project is moving towards the application server serving this content, so this step will not be needed in 
        the future
    </p>
</note>
</procedure>


<procedure title="Start the application server">
<step>
    <p>Open a separate terminal session</p>
</step>
<step>
    <p>In the new session, run</p>
    <code-block lang="bash">
        ./gradlew :playground-js:jvmRun -DmainClass=my.test.ServerPlaygroundAKt --quiet
    </code-block>
    <p>or</p>
    <code-block lang="bash">
        ./gradlew :playground-js:jvmRun -DmainClass=my.test.ServerPlaygroundBKt --quiet
    </code-block>
</step>
</procedure>

#### Modify the playground

You'll find the server playground files in <path>playground-js/src/jvmMain/kotlin/my/test/</path>. Feel free to modify
<path>ServerPlaygroundA.kt</path> or <path>ServerPlaygroundB.kt</path> and rerun the corresponding application server. Have fun.
