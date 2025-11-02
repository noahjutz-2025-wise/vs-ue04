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
