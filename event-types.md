# Chat Event Types

## Common Keys

Keys that exist on every event type.

* __type__ - the type of the event
* __account-id__ - the id of the account this event is associated with (currently the jid)
* __to__ - the jid of the user that is the target of the event (nil?)
* __to-resource__ - the resource of the user that is the target of the event (nil?)
* __from__ - the jid of the user that originiated the event (nil?)
* __from-resource__ - the resource of the user that originiated the event (nil?)

## Incoming

Events coming into Bond from the XMPP chat module.

#### Status Update Event

This event is driven by the prescence stanza and indicates a contacts current status.

* __type__ - :status-update
* __status__ - Either :offline, :online, or :away

#### Roster Event

This event contains a roster entry for every contact and is driven by the roster stanza.

* __type__ - :roster
* __roster__ - Vector of roster entries
* __entry.jid__ - Jid for contact
* __entry.name__ - Display name for contact

#### Message Event

This event represents a message sent from one user to another and is driven by the message stanza.

* __type__ - :message
* __message__ - message text

#### Activity Event

This event represents an action from the other user such as typing, etc. and is driven by the message stanza as well.

* __type__ - :activity
* __activity__ - Either :typing, :paused, or :none

#### Disconnected Event

This event represents a disconnection from the xmpp server irregardless of the cause.

* __type__ - :disconnected

#### Connected Event

This event represents a connection to the xmpp server.

* __type__ - :connected

## Outgoing

Events going out from bond to the XMPP module

#### Disconnect Event

* __type__ - :disconnect

#### Connect Event

* __type__ - :connect

#### Roster Request Event

* __type__ - :roster-request

#### Send Message Event

* __type__ - :message-send
* __message__ - message text