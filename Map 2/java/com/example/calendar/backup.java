    //Thread thread = new Thread(mutiThread);
    //thread.start();

    /*
    private Runnable mutiThread = new Runnable(){
        public void run(){

            SQLDB sqldb = new SQLDB();

        }
    };
    */



    /*
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {



        }
   }, 1000);
     */

    /*
        private class task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... param) {
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
        }
    }
     */



    /*
    //Routeing
    ...implements  RoutingListener {...

    Routing routing = new Routing.Builder()
                                .travelMode(AbstractRouting.TravelMode.DRIVING)
                                .withListener(OpenMapActivity.this)
                                .waypoints(new LatLng(pointFrom.latitude,pointFrom.longitude),new LatLng(pointTo.latitude,pointTo.longitude))
                                .build();
                        routing.execute();

@Override
    public void onRoutingFailure(RouteException e)
    {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            System.out.println("Error: " + e.getMessage());
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() { }

    @Override
    public void onRoutingCancelled()
    {
        Toast.makeText(OpenMapActivity.this, "Routing Cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> list, int i)
    {
        try
        {
            //Get all points and plot the polyLine route.
            List<LatLng> listPoints = list.get(0).getPoints();
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            Iterator<LatLng> iterator = listPoints.iterator();
            while(iterator.hasNext())
            {
                LatLng data = iterator.next();
                options.add(data);
            }

            //If line not null then remove old polyline routing.
            if(polyline != null)
            {
                polyline.remove();
            }
            polyline = mMap.addPolyline(options);

            //Show distance and duration.
            txtDistance.setText("Distance: " + list.get(0).getDistanceText());
            txtTime.setText("Duration: " + list.get(0).getDurationText());

            //Focus on map bounds
            mMap.moveCamera(CameraUpdateFactory.newLatLng(list.get(0).getLatLgnBounds().getCenter()));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(currentLatLng);
            builder.include(Constants.POINT_DEST);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
                catch (Exception e)
                {
                Toast.makeText(OpenMapActivity.this, "EXCEPTION: Cannot parse routing response", Toast.LENGTH_SHORT).show();
                }
        }
     */