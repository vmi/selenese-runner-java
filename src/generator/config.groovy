driver {
    aliases = "-d"
    metaVar = "<driver>"
    usage = "firefox (default) | chrome | ie | edge | safari | htmlunit | remote | appium | FQCN-of-WebDriverFactory"
}

headless {
    usage = "use headless mode if driver is supported (currently, Chrome and Firefox)"
    type = "Boolean"
    driverOption = true
}


profile {
    aliases = "-p"
    metaVar = "<name>"
    usage = "profile name (Firefox only *1)"
    driverOption = true
}

profile_dir {
    aliases = "-P"
    metaVar = "<dir>"
    usage = "profile directory (Firefox only *1)"
    driverOption = true
}

chrome_experimental_options {
    metaVar = "<file>"
    usage = "path to json file specify experimental options for chrome (Chrome only *1)"
    driverOption = true
}

chrome_extension {
    metaVar = "<file>"
    usage = "chrome extension file (multiple, Chrome only *1)"
    type = "String[]"
    driverOption = true
}

proxy_type {
    metaVar = "<proxy-type>"
    usage = "proxy type (manual (default if set --proxy) | pac | autodetect | system)"
    driverOption = true
}

proxy {
    metaVar = "<proxy>"
    usage = "[manual] proxy host and port (HOST:PORT) (excepting IE) / [pac] PAC URL"
    driverOption = true
}

proxy_user {
    metaVar = "<user>"
    usage = "proxy username (HtmlUnit only *2)"
    driverOption = true
}

proxy_password {
    metaVar = "<password>"
    usage = "proxy password (HtmlUnit only *2)"
    driverOption = true
}

no_proxy {
    metaVar = "<hosts>"
    usage = "no-proxy hosts"
    driverOption = true
}

cli_args {
    metaVar = "<arg>"
    usage = "add command line arguments at starting up driver (multiple)"
    type = "String[]"
    driverOption = true
}

remote_url {
    metaVar = "<url>"
    usage = "Remote test runner URL (Remote only)"
    driverOption = true
}

remote_platform {
    metaVar = "<platform>"
    usage = "Desired remote platform (Remote only)"
    driverOption = true
}

remote_browser {
    metaVar = "<browser>"
    usage = "Desired remote browser (Remote only)"
    driverOption = true
}

remote_version {
    metaVar = "<browser-version>"
    usage = "Desired remote browser version (Remote only)"
    driverOption = true
}

highlight {
    aliases = "-H"
    usage = "highlight locator always."
    type = "Boolean"
}

interactive {
    aliases = "-i"
    usage = "interactive mode."
    type = "Boolean"
}

screenshot_dir {
    aliases = "-s"
    metaVar = "<dir>"
    usage = "override captureEntirePageScreenshot directory."
}

screenshot_all {
    aliases = "-S"
    metaVar = "<dir>"
    usage = "take screenshot at all commands to specified directory."
}

screenshot_on_fail {
    metaVar = "<dir>"
    usage = "take screenshot on fail commands to specified directory."
}

screenshot_scroll_timeout {
    metaVar = "<timeout>"
    usage = "set scroll timeout (ms) for taking screenshot. (default: 100)"
}

ignore_screenshot_command {
    usage = "ignore captureEntirePageScreenshot command."
    type = "Boolean"
}

baseurl {
    aliases = "-b"
    metaVar = "<baseURL>"
    usage = "override base URL set in selenese."
}

firefox {
    metaVar = "<path>"
    usage = "path to 'firefox' binary. (implies '--driver firefox')"
    driverOption = true
}

geckodriver {
    metaVar = "<path>"
    usage = "path to 'geckodriver' binary. (implies '--driver firefox')"
    driverOption = true
}

chromedriver {
    metaVar = "<path>"
    usage = "path to 'chromedriver' binary. (implies '--driver chrome')"
    driverOption = true
}

iedriver {
    metaVar = "<path>"
    usage = "path to 'IEDriverServer' binary. (implies '--driver ie')"
    driverOption = true
}

edgedriver {
    metaVar = "<path>"
    usage = "path to Edge 'WebDriver' binary. (implies '--driver edge')"
    driverOption = true
}

xml_result {
    metaVar = "<dir>"
    usage = "output XML JUnit results to specified directory."
}

html_result {
    metaVar = "<dir>"
    usage = "output HTML results to specified directory."
}

timeout {
    aliases = "-t"
    metaVar = "<timeout>"
    usage = "set timeout (ms) for waiting. (default: %DEFAULT_TIMEOUT_MILLISEC_N% ms)"
}

max_retries {
    metaVar = "<maxRetries>"
    usage = "set maximum number of retries for a given step. (default: %DEFAULT_MAX_RETRIES%)"
}

set_speed {
    metaVar = "<speed>"
    usage = "same as executing setSpeed(ms) command first."
}

height {
    metaVar = "<height>"
    usage = "set initial height. (excluding mobile)"
    driverOption = true
}

width {
    metaVar = "<width>"
    usage = "set initial width. (excluding mobile)"
    driverOption = true
}

alerts_policy {
    usage = "The default behaviour for unexpected alerts (accept/ignore/dismiss)"
    driverOption = "UnexpectedAlertBehaviour"
}

define {
    aliases = "-D"
    metaVar = "<key>[:<type>][+]=<value>"
    usage = "define parameters for capabilities. <type> is a value type: str (default), int or bool (multiple)"
    type = "String[]"
    driverOption = true
}

var {
    aliases = "-V"
    metaVar = "<var-name>=<json-value>"
    usage = "set JSON value to variable with a specified name. (multiple)"
    type = "String[]"
}

rollup {
    metaVar = "<file>"
    usage = "define rollup rule by JavaScript. (multiple)"
    type = "String[]"
}

cookie_filter {
    metaVar = "<+RE|-RE>"
    usage = "filter cookies to log by RE matching the name. (\"+\" is passing, \"-\" is suppressing)"
}

log_filter {
    metaVar = "<+type|-type>"
    usage = "filter the logging information by the specified type. (multiple. \"+\" is passing, \"-\" is suppressing. type: cookie, title, url, pageinfo(= cookie & title & url))"
    type = "String[]"
}

command_factory {
    metaVar = "<FQCN>"
    usage = "register user defined command factory. (See Note *3)"
}

no_exit {
    usage = "don't call System.exit at end."
    type = "Boolean"
}

strict_exit_code {
    usage = "return strict exit code, reflected by selenese command results at end. (See Note *4)"
    type = "Boolean"
}

max_time {
    metaVar = "<max-time>"
    usage = "Maximum time in seconds that you allow the entire operation to take."
}

no_replace_alert_method {
    usage = "disable replacement of alert methods"
    type = "Boolean"
}

help {
    aliases = "-h"
    usage = "show this message."
    type = "Boolean"
}
