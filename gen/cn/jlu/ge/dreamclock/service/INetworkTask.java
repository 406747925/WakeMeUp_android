/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/knight/workspace/android/WakeMeUp_android/src/cn/jlu/ge/dreamclock/service/INetworkTask.aidl
 */
package cn.jlu.ge.dreamclock.service;
public interface INetworkTask extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements cn.jlu.ge.dreamclock.service.INetworkTask
{
private static final java.lang.String DESCRIPTOR = "cn.jlu.ge.dreamclock.service.INetworkTask";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an cn.jlu.ge.dreamclock.service.INetworkTask interface,
 * generating a proxy if needed.
 */
public static cn.jlu.ge.dreamclock.service.INetworkTask asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof cn.jlu.ge.dreamclock.service.INetworkTask))) {
return ((cn.jlu.ge.dreamclock.service.INetworkTask)iin);
}
return new cn.jlu.ge.dreamclock.service.INetworkTask.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getFiveDaysWeather:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getFiveDaysWeather(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getFiveDaysWeatherFromNet:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.getFiveDaysWeatherFromNet(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements cn.jlu.ge.dreamclock.service.INetworkTask
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String getFiveDaysWeather(java.lang.String weatherCity) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(weatherCity);
mRemote.transact(Stub.TRANSACTION_getFiveDaysWeather, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void getFiveDaysWeatherFromNet(java.lang.String weatherCity) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(weatherCity);
mRemote.transact(Stub.TRANSACTION_getFiveDaysWeatherFromNet, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getFiveDaysWeather = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getFiveDaysWeatherFromNet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public java.lang.String getFiveDaysWeather(java.lang.String weatherCity) throws android.os.RemoteException;
public void getFiveDaysWeatherFromNet(java.lang.String weatherCity) throws android.os.RemoteException;
}
