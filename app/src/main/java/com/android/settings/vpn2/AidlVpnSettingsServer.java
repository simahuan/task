/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/kings/studio1/FkVPN/simpleserver/src/main/aidl/com/android/settings/vpn2/AidlVpnSettingsServer.aidl
 */
package com.android.settings.vpn2;
// Declare any non-default types here with import statements

public interface AidlVpnSettingsServer extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements AidlVpnSettingsServer {
        private static final String DESCRIPTOR = "com.android.settings.vpn2.AidlVpnSettingsServer";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.android.settings.vpn2.AidlVpnSettingsServer interface,
         * generating a proxy if needed.
         */
        public static AidlVpnSettingsServer asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof AidlVpnSettingsServer))) {
                return ((AidlVpnSettingsServer) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_connectVpn: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    String _arg2;
                    _arg2 = data.readString();
                    String _arg3;
                    _arg3 = data.readString();
                    String _result = this.connectVpn(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_disconnectVpn: {
                    data.enforceInterface(DESCRIPTOR);
                    this.disconnectVpn();
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements AidlVpnSettingsServer {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public String connectVpn(String name, String server, String username, String password) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(server);
                    _data.writeString(username);
                    _data.writeString(password);
                    mRemote.transact(Stub.TRANSACTION_connectVpn, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void disconnectVpn() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_disconnectVpn, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_connectVpn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_disconnectVpn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    public String connectVpn(String name, String server, String username, String password) throws android.os.RemoteException;

    public void disconnectVpn() throws android.os.RemoteException;
}
