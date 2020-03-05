package com.codepros.prohub.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataseHelper {
    private FirebaseDatabase myDatabase;
    private DatabaseReference myUserRef;
    private DatabaseReference myPropRef;
    private DatabaseReference myUnitRef;

    // list of data
    private List<User> users = new ArrayList<>();
    private List<Property> properties = new ArrayList<>();
    private List<Unit> units = new ArrayList<>();

    // interface to load User database
    public interface UserDataStatus{
        void DataIsLoad(List<User> users, List<String> keys);
    }

    // interface to load Property database
    public interface PropDataStatus{
        void DataIsLoad(List<Property> properties, List<String> keys);
    }

    // interface to load Unit database
    public interface UnitDataStatus{
        void DataIsLoad(List<Unit> units, List<String> keys);
    }

    public FirebaseDataseHelper(){
        myDatabase = FirebaseDatabase.getInstance();
        myUserRef = myDatabase.getReference("users");
        myPropRef = myDatabase.getReference("properties");
        myUnitRef = myDatabase.getReference("units");
    }

    public void readUsers(final UserDataStatus dataStatus){
        myUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    User user = keyNode.getValue(User.class);
                    users.add(user);
                }
                dataStatus.DataIsLoad(users, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void readProperty(final PropDataStatus dataStatus)
    {
        myPropRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                properties.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Property property = keyNode.getValue(Property.class);
                    properties.add(property);
                }
                dataStatus.DataIsLoad(properties, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void readUnits(final UnitDataStatus unitDataStatus){
        myUnitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                units.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Unit unit = keyNode.getValue(Unit.class);
                    units.add(unit);
                }
                unitDataStatus.DataIsLoad(units, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
