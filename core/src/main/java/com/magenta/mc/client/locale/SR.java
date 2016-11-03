package com.magenta.mc.client.locale;

import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.util.StringLoader;

import java.util.Hashtable;

/**
 * Created 27.02.2010
 *
 * @author Konstantin Pestrikov
 */
public class SR {

    public final static String MS_SLASHME = "/me";
    public static String MS_XMLLANG;
    public static String MS_IFACELANG;
    private static Hashtable presences;
    private static Hashtable lang;
    public static String MS_DISCO = loadString("Service.Discovery");
    public static String MS_USER_JID = loadString("User.JID");
    public static String MS_NEW_LIST = loadString("New.list");
    public static String MS_NOLOGIN = loadString("Select.no.login");
    public static String MS_PRIVACY_RULE = loadString("Privacy.rule");
    public static String MS_SSL = loadString("use.SSL");
    public static String MS_MODIFY = loadString("Modify");
    public static String MS_UPDATE = loadString("Update");
    public static String MS_ACCOUNT_NAME = loadString("Account.name");
    public static String MS_GMT_OFFSET = loadString("GMT.offset");
    public static String MS_TIME_SETTINGS = loadString("Time.settings.hours");
    public static String MS_CONNECTED = loadString("Connected");
    public static String MS_CONNECT_TO = loadString("Connect.to");
    public static String MS_ALERT_PROFILE = loadString("Alert.Profile");
    public static String MS_MOVE_UP = loadString("Move.Up");
    public static String MS_OWNERS = loadString("Owners");
    public static String MS_OK = loadString("Ok");
    public static String MS_APP_MINIMIZE = loadString("Minimize");
    public static String MS_ROOM = loadString("Room");
    public static String MS_MESSAGES = loadString("Messages");
    public static String MS_REFRESH = loadString("Refresh");
    public static String MS_RESOLVE_NICKNAMES = loadString("Resolve Nicknames");
    public static String MS_PRIVACY_ACTION = loadString("Action");
    public static String MS_BAN = loadString("Ban");
    public static String MS_LEAVE_ROOM = loadString("Leave.Room");
    public static String MS_PASSWORD = loadString("Password");
    public static String MS_ITEM_ACTIONS = loadString("Actions>");
    public static String MS_ACTIVATE = loadString("Activate");
    public static String MS_AFFILIATION = loadString("Affiliation");
    public static String MS_ACCOUNTS = loadString("Accounts");
    public static String MS_DELETE_LIST = loadString("Delete.list");
    public static String MS_ACCOUNT_ = loadString("Account>");
    //public   static String MS_SHOWOFFLINES = loadString( "Show Offlines" );
    public static String MS_SELECT = loadString("Select");
    public static String MS_SUBJECT = loadString("Subject");
    public static String MS_GROUP_MENU = loadString("Group.menu");
    public static String MS_APP_QUIT = loadString("Quit");
    public static String MS_EDIT_LIST = loadString("Edit.list");
    public static String MS_REGISTERING = loadString("Registering");
    public static String MS_DONE = loadString("Done");
    public static String MS_ERROR_ = loadString("Error:");
    public static String MS_BROWSE = loadString("Browse");
    public static String MS_SAVE_LIST = loadString("Save.list");
    public static String MS_KEEPALIVE_PERIOD = loadString("Keep-Alive.period");
    public static String MS_NEWGROUP = loadString("<New.Group>");
    public static String MS_SEND = loadString("Send");
    public static String MS_PRIORITY = loadString("Priority");
    public static String MS_FAILED = loadString("Failed");
    public static String MS_SET_PRIORITY = loadString("Set.Priority");
    public static String MS_DELETE_RULE = loadString("Delete.rule");
    public static String MS_IGNORE_LIST = loadString("Ignore-List");
    public static String MS_ROSTER_REQUEST = loadString("Roster.request");
    public static String MS_PRIVACY_TYPE = loadString("Type");
    public static String MS_NAME = loadString("Name");
    public static String MS_USERNAME = loadString("Username");
    public static String MS_FULLSCREEN = loadString("fullscreen");
    public static String MS_ADD_BOOKMARK = loadString("Add.bookmark");
    public static String MS_CONFERENCES_ONLY = loadString("conferences.only");
    public static String MS_CLIENT_INFO = loadString("Client.Version");
    public static String MS_DISCARD = loadString("Discard.Search");
    public static String MS_SEARCH_RESULTS = loadString("Search.Results");
    public static String MS_GENERAL = loadString("General");
    public static String MS_MEMBERS = loadString("Members");
    public static String MS_ADD_CONTACT = loadString("Add Passenger");
    public static String MS_SUBSCRIPTION = loadString("Subscription");
    public static String MS_STATUS_MENU = loadString("Status >");
    public static String MS_JOIN = loadString("Join");
    public static String MS_STARTUP_ACTIONS = loadString("Startup actions");
    public static String MS_SERVER = loadString("Server");
    public static String MS_ADMINS = loadString("Admins");
    public static String MS_MK_ILIST = loadString("Make Ignore-List");
    public static String MS_OPTIONS = loadString("Options");
    public static String MS_DELETE = loadString("Delete");
    public static String MS_DELETE_ASK = loadString("Delete contact?");
    public static String MS_SUBSCRIBE = loadString("Authorize");
    public static String MS_NICKNAMES = loadString("Nicknames");
    //public   static String MS_ENT_SETUP = loadString( "Entering setup" );
    public static String MS_ADD_ARCHIVE = loadString("to Archive");
    public static String MS_BACK = loadString("Back");
    public static String MS_HEAP_MONITOR = loadString("heap monitor");
    public static String MS_MESSAGE = loadString("Message");
    public static String MS_OTHER = loadString("<Other>");
    public static String MS_HISTORY = loadString("history -");
    public static String MS_APPEND = loadString("Append");
    public static String MS_ACTIVE_CONTACTS = loadString("Active Contacts");
    public static String MS_SELECT_NICKNAME = loadString("Select nickname");
    public static String MS_GROUP = loadString("Group");
    public static String MS_JOIN_CONFERENCE = loadString("Join conference");
    public static String MS_NO = loadString("No");
    public static String MS_REENTER = loadString("Re-Enter Room");
    public static String MS_NEW_MESSAGE = loadString("New Message");
    public static String MS_ADD = loadString("Add");
    public static String MS_LOGON = loadString("Logon");
    public static String MS_STANZAS = loadString("Stanzas");
    public static String MS_AT_HOST = loadString("at Host");
    public static String MS_AUTO_CONFERENCES = loadString("join conferences");
    public static String MS_STATUS = loadString("Status");
    public static String MS_SMILES_TOGGLE = loadString("Smiles");
    public static String MS_CONTACT = loadString("Passenger >");
    public static String MS_OFFLINE_CONTACTS = loadString("offline contacts");
    public static String MS_TRANSPORT = loadString("Transport");
    public static String MS_COMPOSING_EVENTS = loadString("composing events");
    public static String MS_ADD_SMILE = loadString("Add Smile");
    public static String MS_NICKNAME = loadString("Nickname");
    public static String MS_REVOKE_VOICE = loadString("Revoke Voice");
    public static String MS_NOT_IN_LIST = loadString("Not-in-list");
    public static String MS_COMMANDS = loadString("Commands");
    public static String MS_CHSIGN = loadString("- (Sign)");
    public static String MS_SETDEFAULT = loadString("Set default");
    public static String MS_BANNED = loadString("Outcasts (Ban)");
    public static String MS_SET_AFFILIATION = loadString("Set affiliation to");
    public static String MS_REGISTER_ACCOUNT = loadString("Register Account");
    public static String MS_AUTOLOGIN = loadString("autologin");
    public static String MS_LOGOFF = loadString("Logoff");
    public static String MS_PUBLISH = loadString("Publish");
    public static String MS_SUBSCR_REMOVE = loadString("Remove subscription");
    public static String MS_SET = loadString("Set");
    public static String MS_APPLICATION = loadString("Application");
    public static String MS_BOOKMARKS = loadString("Bookmarks");
    public static String MS_TEST_SOUND = loadString("Test sound");
    public static String MS_STARTUP = loadString("Startup");
    public static String MS_EDIT_RULE = loadString("Edit rule");
    public static String MS_CANCEL = loadString("Cancel");
    public static String MS_CLOSE = loadString("Close");
    public static String MS_ARCHIVE = loadString("Archive");
    public static String MS_FREE = loadString("free ");
    public static String MS_CONFERENCE = loadString("Conference");
    public static String MS_SOUND = loadString("Sound");
    public static String MS_LOGIN_FAILED = loadString("Login failed");
    public static String MS_DISCOVER = loadString("Browse"); //"Discover"
    public static String MS_NEW_JID = loadString("New Jid");
    public static String MS_PLAIN_PWD = loadString("plain-text password");
    public static String MS_PASTE_NICKNAME = loadString("Paste Nickname");
    public static String MS_KICK = loadString("Kick");
    public static String MS_CLEAR_LIST = loadString("Clear List");
    public static String MS_GRANT_VOICE = loadString("Grant Voice");
    public static String MS_MOVE_DOWN = loadString("Move Down");
    public static String MS_QUOTE = loadString("Quote");
    public static String MS_ROSTER_ELEMENTS = loadString("Roster elements");
    public static String MS_ENABLE_POPUP = loadString("popup from background");
    public static String MS_SMILES = loadString("smiles");
    public static String MS_ABOUT = loadString("About");
    public static String MS_RESOURCE = loadString("Resource");
    public static String MS_DISCONNECTED = loadString("Disconnected");
    public static String MS_EDIT = loadString("Edit");
    public static String MS_HOST_IP = loadString("Host name/IP (optional)");
    public static String MS_ADD_RULE = loadString("Add rule");
    public static String MS_ALL_STATUSES = loadString("for all status types");
    public static String MS_PASTE_JID = loadString("Paste Jid");
    public static String MS_GOTO_URL = loadString("Goto URL");
    public static String MS_CLOCK_OFFSET = loadString("Clock offset");
    public static String MS_YES = loadString("Yes");
    public static String MS_FLASHBACKLIGHT = loadString("flash backlight");
    public static String MS_SUSPEND = loadString("Suspend");
    public static String MS_ALERT_PROFILE_CMD = loadString("Alert Profile >");
    public static String MS_MY_VCARD = loadString("My vCard");
    public static String MS_TRANSPORTS = loadString("transports");
    public static String MS_NEW_ACCOUNT = loadString("New Account");
    public static String MS_SELF_CONTACT = loadString("self-contact");
    public static String MS_VCARD = loadString("vCard");
    public static String MS_SET_SUBJECT = loadString("Set Subject");
    public static String MS_TOOLS = loadString("Tools");
    //public   static String MS_JABBER_TOOLS = loadString( "Jabber Tools" ); //replaced by "Tools"
    public static String MS_PORT = loadString("Port");
    public static String MS_RESUME = loadString("Resume Message");
    //public   static String MS_PROXY_ENABLE = loadString( "proxy CONNECT" );
    //public   static String MS_PROXY_HOST = loadString( "Proxy name/IP" );
    //public   static String MS_PROXY_PORT = loadString( "Proxy port" );
    public static String MS_ARE_YOU_SURE_WANT_TO_DISCARD = loadString("Are You sure want to discard ");
    public static String MS_FROM_OWNER_TO = loadString(" from OWNER to ");
    public static String MS_MODIFY_AFFILIATION = loadString("Modify affiliation");
    //public   static String MS_ADD_TO_ROSTER = loadString( "Add to roster" ); //not used in 1197 there are fs#464 ;-) (string don't deleted in locales)
    public static String MS_CLEAR = loadString("Clear");
    public static String MS_SELLOGIN = loadString("Connect");
    //--toon
    public static String MS_UNAFFILIATE = loadString("Unaffiliate");
    //--toon
    public static String MS_GRANT_MODERATOR = loadString("Grant Moderator");
    public static String MS_REVOKE_MODERATOR = loadString("Revoke Moderator");
    public static String MS_GRANT_ADMIN = loadString("Grant Admin");
    public static String MS_GRANT_OWNERSHIP = loadString("Grant Ownership");
    public static String MS_VIZITORS_FORBIDDEN = loadString("Visitors are not allowed to send messages to all occupants");
    public static String MS_IS_INVITING_YOU = loadString(" is inviting You to ");
    public static String MS_ASK_SUBSCRIPTION = loadString("Ask subscription");
    public static String MS_GRANT_SUBSCRIPTION = loadString("Grant subscription");
    public static String MS_INVITE = loadString("Invite to conference");
    public static String MS_REASON = loadString("Reason");
    public static String MS_YOU_HAVE_BEEN_INVITED = loadString("You have been invited to ");
    public static String MS_DISCO_ROOM = loadString("Participants");
    public static String MS_CAPS_STATE = loadString("Abc");
    public static String MS_STORE_PRESENCE = loadString("room presences");
    public static String MS_IS_NOW_KNOWN_AS = loadString(" is now known as ");
    public static String MS_WAS_BANNED = loadString(" was banned ");
    public static String MS_WAS_KICKED = loadString(" was kicked ");
    public static String MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY = loadString(" has been kicked because room became members-only");
    public static String MS_HAS_LEFT_CHANNEL = loadString(" has left the channel");
    public static String MS_HAS_JOINED_THE_CHANNEL_AS = loadString(" has joined the channel as ");
    public static String MS_AND = loadString(" and ");
    public static String MS_IS_NOW = loadString(" is now ");
    //2007-04-11
    public static String MS_AUTOFOCUS = loadString("autofocus");
    public static String MS_GRANT_MEMBERSHIP = loadString("Grant Membership");
    public static String MS_SURE_CLEAR = loadString("Are You sure want to clear messagelist?");
    public static String MS_TOKEN = loadString("Google token request");
    public static String MS_FEATURES = loadString("Features");
    public static String MS_SHOWPWD = loadString("Show password");
    public static String MS_NO_VERSION_AVAILABLE = loadString("No client version available");
    public static String MS_MSG_LIMIT = loadString("Message limit");
    public static String MS_OPENING_STREAM = loadString("Opening stream");
    public static String MS_ZLIB = loadString("Using compression");
    public static String MS_AUTH = loadString("Authenticating");
    public static String MS_RESOURCE_BINDING = loadString("Resource binding");
    public static String MS_SESSION = loadString("Initiating session");
    public static String MS_TEXTWRAP = loadString("Text wrapping");
    public static String MS_TEXTWRAP_CHARACTER = loadString("by chars");
    public static String MS_TEXTWRAP_WORD = loadString("by words");
    public static String MS_INFO = loadString("Info");
    public static String MS_REPLY = loadString("Reply");
    public static String MS_DIRECT_PRESENCE = loadString("Send status");
    public static String MS_CONFIRM_BAN = loadString("Are you sure want to BAN this person?");
    public static String MS_NO_REASON = loadString("No reason");
    public static String MS_RECENT = loadString("Recent");
    public static String MS_CAMERASHOT = loadString("Shot");
    public static String MS_SELECT_FILE = loadString("Select file");
    public static String MS_LOAD_PHOTO = loadString("Load Photo");
    public static String MS_CLEAR_PHOTO = loadString("Clear Photo");
    public static String MS_CAMERA = loadString("Camera");
    public static String MS_HIDE_FINISHED = loadString("Hide finished");
    public static String MS_TRANSFERS = loadString("Transfer tasks");
    public static String MS_SURE_DELETE = loadString("Are you sure want to delete this message?");
    public static String MS_NEW_BOOKMARK = loadString("New conference");
    public static String MS_ROOT = loadString("Root");
    public static String MS_DECLINE = loadString("Decline");
    public static String MS_AUTH_NEW = loadString("Authorize new contacts");
    public static String MS_AUTH_AUTO = loadString("[auto-subscribe]");
    public static String MS_KEEPALIVE = loadString("Keep-Alive");
    public static String MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM = loadString(" has been unaffiliated and kicked from members-only room");
    public static String MS_AWAY_PERIOD = loadString("Minutes before away");
    public static String MS_AWAY_TYPE = loadString("Automatic Away");


//2007-04-12

    //public   static String MS_LOW_CASE_ROLE = loadString( "role" );
    //public   static String MS_LOW_CASE_AFFILIATION = loadString( "affiliation" );
    //public   static String MS_VISITOR = loadString( "visitor" );
    //public   static String MS_PARTICIPANT = loadString( "participant" );
    //public   static String MS_MODERATOR = loadString( "moderator" );
    //public   static String MS_OWNER = loadString( "owner" );
    //public   static String MS_ADMIN = loadString( "admin" );
    //public   static String MS_MEMBER = loadString( "member" );
    public static String MS_AWAY_OFF = loadString("disabled");
    public static String MS_AWAY_LOCK = loadString("keyblock / flip");
    public static String MS_AWAY_IDLE = loadString("idle");
    public static String MS_ADD_AUTOJ = loadString("Add autojoin bookmark");
    public static String MS_DO_AUTOJOIN = loadString("Join marked (auto)"); //temporary
    public static String MS_SHOW_HARDWARE = loadString("shared platform info");
    public static String MS_DELIVERY = loadString("delivery events");
    public static String MS_NIL_DROP_MP = loadString("drop all");
    public static String MS_NIL_DROP_P = loadString("receive messages");
    public static String MS_NIL_ALLOW_ALL = loadString("messages & presences");
    public static String MS_FONTSIZE_NORMAL = loadString("normal");
    public static String MS_FONTSIZE_SMALL = loadString("small");
    public static String MS_FONTSIZE_LARGE = loadString("large");
    public static String MS_FILE_TRANSFERS = loadString("File Transfers");
    public static String MS_ALERT_PROFILE_AUTO = loadString("Auto");
    public static String MS_ALERT_PROFILE_ALLSIGNALS = loadString("All signals");
    public static String MS_ALERT_PROFILE_VIBRA = loadString("Vibra");
    //public static String MS_ALERT_PROFILE_SOUND = loadString( "Sound" );
    public static String MS_ALERT_PROFILE_NOSIGNALS = loadString("No signals");
    public static String MS_IS_DEFAULT = loadString(" (default)");
    //2007-10-24 voffk
    public static String MS_MESSAGE_COLLAPSE_LIMIT = loadString("Message collapse limit");
    public static String MS_SUBSCRIPTION_REQUEST_FROM_USER = loadString("This user wants to subscribe to your presence");
    public static String MS_SUBSCRIPTION_RECEIVED = loadString("You are now authorized");
    public static String MS_SUBSCRIPTION_DELETED = loadString("Your authorization has been removed!");
    public static String MS_CHANGE_NICKNAME = loadString("Change nickname");
    public static String MS_HAS_CHANGED_SUBJECT_TO = loadString(" has changed subject to: ");
    public static String MS_SEND_FILE = loadString("Send file");
    public static String MS_SEND_FILE_TO = loadString("To: ");
    public static String MS_FILE = loadString("File");
    public static String MS_DESCRIPTION = loadString("Description");
    public static String MS_PATH = loadString("Path");
    public static String MS_ACCEPT_FILE = loadString("Accept file");
    public static String MS_SAVE_TO = loadString("Save to");
    public static String MS_SENDER = loadString("Sender:");
    public static String MS_FILE_SIZE = loadString("size:");
    //2007-11-04
    public static String MS_SUBSCR_AUTO = loadString("Automatic subscription");
    public static String MS_SUBSCR_ASK = loadString("Ask me");
    public static String MS_SUBSCR_DROP = loadString("Drop subscription");
    public static String MS_SUBSCR_REJECT = loadString("Deny subscription");  //TODO: correct according to RFC
    //2007-11-07
    public static String MS_SEARCH = loadString("Search");
    public static String MS_REGISTER = loadString("Register");
    public static String MS_COLOR_THEME = loadString("Color theme");
    public static String MS_MEMORY = loadString("Memory:");
    public static String MS_MEMORY_FREE = loadString("Free=");
    public static String MS_MEMORY_TOTAL = loadString("Total=");
    public static String MS_VERSIONS = loadString("Versions");
    public static String MS_INSTALL = loadString("Install");
    public static String MS_AVAILABLE_VERSIONS = loadString("Available versions");
    //2008-01-16
    public static String MS_USER = loadString("User");
    //2008-05-04
    public static String MS_NO_COMPRESSION = loadString("No compression");
    public static String MS_NEW_ROOM_CREATED = loadString("New room created");
    public static String MS_LOAD_ROOMLIST = loadString("Browse rooms");
    public static String MS_PEP = loadString("Personal events");
    public static String MS_USERMOOD = loadString("User Mood");
    public static String MS_PEP_NOT_SUPPORTED = loadString("Personal events not supported");
    public static String MS_PASTE = loadString("Paste");
    public static String MS_PRESENCE_ONLINE = loadString("online");

    private SR() {
    }

    private synchronized static void loadLang() {
        if (lang == null) {
            String langFile = "";//Config.getInstance().langFileName();
            MCLoggerFactory.getLogger(SR.class).debug("Loading locale " + langFile);
            if (langFile != null) lang = new StringLoader().hashtableLoader(langFile);
            if (lang == null) lang = new Hashtable();

            MS_XMLLANG = (String) lang.get("xmlLang");

            MS_IFACELANG = MS_XMLLANG;
            if (MS_IFACELANG == null) MS_IFACELANG = "en";
        }
    }

    private static String loadString(String key) {
        if (lang == null) loadLang();
        String value = (String) lang.get(key);
//#if LOCALE_DEBUG
        if (value == null) {
            if (!lang.isEmpty()) {
                System.out.print("Can't find local string for <");
                System.err.print(key);
                System.err.println('>');
            }
        }
//#endif
        return (value == null) ? key : value;
    }

    public static String getPresence(String presenceName) {
        if (presences == null) {
            presences = new Hashtable();
            presences.put("online", MS_PRESENCE_ONLINE);
            presences.put("chat", loadString("free for chat"));
            presences.put("away", loadString("away"));
            presences.put("xa", loadString("not available"));
            presences.put("invisible", loadString("invisible"));
            presences.put("dnd", loadString("do not disturb"));
            presences.put("unavailable", loadString("offline"));
        }
        String result = (String) presences.get(presenceName);
        if (result == null) result = MS_PRESENCE_ONLINE;
        return result;
    }

    public static void loaded() {
        lang = null;
    }

}
