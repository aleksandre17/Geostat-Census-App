package com.geostat.census_2024.ui.fragment.stepper.adapter;

import com.geostat.census_2024.R;
import com.geostat.census_2024.databinding.TestItemBinding;

public class ExitAdapter {
}
//
//public class ListItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
//
//    private Activity activity;
//    public ObservableList<HouseHoldModel> m;
//
//    public AddressingModel mmm;
//    private LifecycleOwner lifecycleOwner;
//
//    private IfHolderChangeHandler Listener;
//
//    public ListItemAdapter(@NonNull Activity activity, LifecycleOwner lifecycleOwner) {
//        this.activity = activity;
//        this.lifecycleOwner = lifecycleOwner;
//        setHasStableIds(true);
//
//    }
//
//    public void setListener (IfHolderChangeHandler Listener) {
//        this.Listener = Listener;
//    }
//
//
//    @NonNull
//    @Override
//    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        TestItemBinding testItemBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.test_item, parent, false);
//        return new ItemViewHolder(testItemBinding, lifecycleOwner);
//    }
//
//    public void submit(final ObservableList<HouseHoldModel> model, AddressingModel mm) {
//
//        Log.d("RESULTRRESULT", "submit: 1" + model);
//        Log.d("RESULTRRESULT", "submit: 2" + m);
//
//        mmm = mm;
//
//        if (this.m == null) {
//
//            this.m = model;
//            notifyItemRangeInserted(0, model.size());
//
//        } else {
//
//            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                @Override
//                public int getOldListSize() {
//                    return m.size();
//                }
//
//                @Override
//                public int getNewListSize() {
//                    return model.size();
//                }
//
//                @Override
//                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                    return m.get(oldItemPosition).getId() == model.get(newItemPosition).getId();
//                }
//
//                @Override
//                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//
////                        HouseHoldModel newProduct = model.get(newItemPosition);
////                        HouseHoldModel oldProduct = m.get(oldItemPosition);
//
//                    return false;
////                        return newProduct.getLastName() == oldProduct.getLastName() && newProduct.getFirstName() == oldProduct.getFirstName() && newProduct.getFatherName() == oldProduct.getFatherName() && newProduct.getId() == oldProduct.getId();
//                }
//            });
//
//            this.m = model;
//            result.dispatchUpdatesTo(this);
//
//        }
//    }
//
//    @Override
//    public long getItemId(int position) {
//        Log.d("RESULTRRESULT", "getItemId: " + m);
//        return m.get(position).getId();
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
//
//        HouseHoldModel houseHoldModel = m.get(position);
//
//        Log.d("RESULTRRESULT", "onBindViewHolder: " + houseHoldModel);
//
//        HouseHoldModel tmp = null;
//        try {
//            tmp = (HouseHoldModel) houseHoldModel.clone();
//            holder.bindItem(houseHoldModel, mmm, m, position, Listener);
//            holder.binding.setLifecycleOwner(getViewLifecycleOwner());
//            holder.binding.executePendingBindings();
//
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        // Log.d("UPS", "getItemCount: " + m.size());
//        return this.m != null ? this.m.size() : 0;
//    }
//}
//
//private static class ItemViewHolder extends RecyclerView.ViewHolder{
//
//    TestItemBinding binding;
//    List<MutableLivedata<HouseHoldModel>> m;
//    LifecycleOwner lifecycleOwner;
//
//    public static Integer flag = 0;
//
//    public ItemViewHolder(TestItemBinding binding, LifecycleOwner lifecycleOwner) {
//        super(binding.getRoot());
//        this.binding = binding;
//
//        this.lifecycleOwner = lifecycleOwner;
//
//    }
//
//    void bindItem(HouseHoldModel item, AddressingModel mmm, ObservableList<HouseHoldModel> m, int index, IfHolderChangeHandler listener) {
//
//        item.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                Log.d("RESULTRRESULT", "onPropertyChanged: " + sender);
//                Log.d("RESULTRRESULT", "onPropertyChanged: " + m.get(index));
//
//
//                if (mmm.getHouseHold() != null) {
//
//                    Log.d("RESULTRRESULT", "onPropertyChanged: " + mmm);
//
////                        if (flag == 0) {
////                            item.removeOnPropertyChangedCallback(this);
////                            new Handler().post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    flag++;
////                                    listener.ifHolderChange(index, (HouseHoldModel) sender);
////                                }
////                            });
////                        } else {
////                            flag = 0;
////                        }
//
//                }
//            }
//        });
//
//        binding.setVm(item);
//
//    }
//
//    public boolean areSome (HouseHoldModel newitEM, HouseHoldModel OLDitEM) {
//
//        Log.d("IFIFIFIFIF", "areSome: " + newitEM);
//        Log.d("IFIFIFIFIF", "areSome: " + OLDitEM);
//
//        if (OLDitEM==newitEM) return true;
//        if (newitEM == null) return false;
//        if (OLDitEM == null) return false;
//        if (newitEM.getClass() != OLDitEM.getClass()) return false;
//        // Class name is Employ & have lastname
//
//        try {
//            return    !newitEM.getLastName().equals(OLDitEM.getLastName())
//                    || !newitEM.getFirstName().equals(OLDitEM.getFirstName())
//                    || !newitEM.getFatherName().equals(OLDitEM.getFatherName())
//                    || newitEM.getId() != OLDitEM.getId();
//
//        } catch (NullPointerException e) {
//            return false;
//        }
//
//    }
//
//}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//
//
////    public boolean areSome (HouseHoldModel newitEM, HouseHoldModel OLDitEM) {
////
////        if (OLDitEM==newitEM) return true;
////        if (newitEM == null) return false;
////        if (newitEM.getClass() != OLDitEM.getClass()) return false;
////        // Class name is Employ & have lastname
////
////        return newitEM.getLastName().equals(OLDitEM.getLastName())
////                && newitEM.getFirstName().equals(OLDitEM.getFirstName())
////                && newitEM.getFatherName().equals(OLDitEM.getFatherName())
////                && newitEM.getId() == OLDitEM.getId();
////
////
////    }
//
////        if (fragmentStepViewModel.getTmpHouseHolder().size() == 0) {
////            HouseHoldModel m = new HouseHoldModel();
////            m.setFatherName("111");
////            m.setId(1);
////
////            MutableLivedata<HouseHoldModel> seter = new MutableLivedata<>();
////            seter.setValue(m);
////
////            fragmentStepViewModel.addTrigger(seter);
////        }
//
//
////    ObservableList<MutableLiveData<HouseHoldModel>> m = new ObservableArrayList<>();
////
////                model.forEach(new Consumer<MutableLiveData<HouseHoldModel>>() {
////@Override
////public void accept(MutableLiveData<HouseHoldModel> houseHoldModelMutableLivedata) {
////        HouseHoldModel mmd = houseHoldModelMutableLivedata.getValue();
////        MutableLivedata<HouseHoldModel> mutableLivedata = new MutableLivedata<>();
////        mutableLivedata.setValue(mmd);
////        m.add(mutableLivedata);
////        }
////        });
//
////
////Log.d("HUSHHUSH", "submit: " + model.toString());
////
////
////        ObservableList<MutableLiveData<HouseHoldModel>> m = new ObservableArrayList<>();
////
////        model.forEach(new Consumer<MutableLiveData<HouseHoldModel>>() {
////@Override
////public void accept(MutableLiveData<HouseHoldModel> houseHoldModelMutableLivedata) {
////        HouseHoldModel mmd = houseHoldModelMutableLivedata.getValue();
////        MutableLivedata<HouseHoldModel> mutableLivedata = new MutableLivedata<>();
////        mutableLivedata.setValue(mmd);
////        m.add(mutableLivedata);
////        }
////        });
//
//
////item.observeForever(new Observer<HouseHoldModel>() {
////@Override
////public void onChanged(HouseHoldModel houseHoldModel) {
////        binding.setVm(houseHoldModel);
////
////        if (mmm.getHouseHold() != null) {
////
////        new Handler().postDelayed(new Runnable() {
////@Override
////public void run() {
////        listener.ifHolderChange(index, m.get(index));
////        }
////        }, 2000);
////        }
////
////        item.removeObserver(this);
////        }
////        });