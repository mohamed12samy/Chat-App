package com.example.chatapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class UserViewModel extends ViewModel {

    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private Repository mainRepository;
    public UserViewModel (){
        mainRepository = Repository.getInstance();
    }

    public LiveData<List<User>> getUsers(){

        users = (MutableLiveData<List<User>>) mainRepository.getUsers();

        return users;
    }
public void forqardMessage(String message,String image,String receiver){
       mainRepository.forwardMessage(message,image, receiver);
    }

}
