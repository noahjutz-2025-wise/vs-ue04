# Direct Messages Server

## Protocol

### Request

- Request Type
  - 00 Register
  - 01 Send
  - 10 Get

Register Frame

```
+--+--------+--+
|00|Username|\0|
+--+--------+--+
```

Send Frame

```
+--+--------+--+---------+--+-------+--+
|01|Username|\0|Recipient|\0|Message|\0|
+--+--------+--+---------+--+-------+--+
```

Get Frame

```
+--+--------+--+
|10|Username|\0|
+--+--------+--+
```

### Response

- Status codes
  - 00 OK
  - 01 Request malformed
  - 11 Internal error

Register Frame

```
+--+--+
|00|St|
+--+--+
```

Send Frame

```
+--+--+
|01|St|
+--+--+
```

Get Frame

```
+--+--+------+--+---------+--+-------+--+---+
|10|St|Sender|\0|Recipient|\0|Message|\0|...|
+--+--+------+--+---------+--+-------+--+---+
```
