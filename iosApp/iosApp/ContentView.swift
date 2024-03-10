//
//  ContentView.swift
//  iosApp
//
//  Created by Javier Martin on 10/3/24.
//
import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundColor(.accentColor)
            Text("Hello, world!")
            Text(Greeting().greet())
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
